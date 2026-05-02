package com.moneyforward.gradle

import com.moneyforward.gradle.provider.PackageRepositoryCredentialProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings
import org.gradle.api.logging.Logger
import org.gradle.api.provider.Provider

/**
 * Configures the [private repository configuration][PrivateRepositoryConfiguration] used
 * by the [private-repository-plugin][PrivateRepositoryPlugin].
 *
 * @see PrivateRepositoryConfiguration
 * @see PrivateRepositoryConfiguration.repository
 */
fun RepositoryHandler.private(configure: ProjectPrivateRepositoryConfiguration.() -> Unit) {
    configure(PrivateRepositoryPlugin.PROJECT_PLUGIN_DATA)
}

fun Settings.privatePlugins(configure: PrivateRepositoryConfiguration.() -> Unit) {
    configure(PrivateRepositoryPlugin.SETTINGS_PLUGIN_DATA)
}

/**
 * Configures private repositories for `dependencyResolutionManagement.repositories` from
 * `settings.gradle(.kts)`. Mirrors [privatePlugins] but targets dependency resolution rather than
 * plugin resolution.
 */
fun Settings.privateDependencies(configure: PrivateRepositoryConfiguration.() -> Unit) {
    configure(PrivateRepositoryPlugin.SETTINGS_DEPENDENCY_DATA)
}

/**
 * The main plugin class for `com.moneyforward.private-repository-plugin`
 * This class automatically configures gradle repositories to include dependencies to private
 * packages.
 */
class PrivateRepositoryPlugin : Plugin<Any> {
    companion object {
        internal val PROJECT_PLUGIN_DATA = ProjectPrivateRepositoryConfiguration()
        internal val SETTINGS_PLUGIN_DATA = PrivateRepositoryConfiguration()
        internal val SETTINGS_DEPENDENCY_DATA = PrivateRepositoryConfiguration()
        internal const val USERNAME_PROPERTY = "private-repository.github.username"
        internal const val TOKEN_PROPERTY = "private-repository.github.token"
    }
    private var logger: Logger? = null

    override fun apply(target: Any) {
        when (target) {
            is Project -> apply(target)
            is Settings -> apply(target)
            else -> throw IllegalArgumentException("Invalid application of plugin for ${target::class.simpleName}")
        }
    }

    private fun apply(project: Project) {
        logger = project.logger
        val logger = project.logger

        project.tasks.create(StoreRepositoryCredentialsTask.NAME, StoreRepositoryCredentialsTask::class.java)

        project.afterEvaluate {
            val tasks = it.gradle.startParameter.taskNames
            // do not apply repository settings if running the storeGitHubCredentials task
            if (tasks.none { task -> task == StoreRepositoryCredentialsTask.NAME }) {
                project.repositories.apply(ProjectPropertyDelegate(project), PROJECT_PLUGIN_DATA)
            }
            else if (tasks.size > 1) {
                logger.warn("{} should be ran in isolation, running it with other " +
                        "tasks may cause unexpected issues.", StoreRepositoryCredentialsTask.NAME)
            }
        }
    }

    private fun apply(settings: Settings) {
        // Defer registration until settings.gradle(.kts) finishes evaluating so that
        // privatePlugins { ... } / privateDependencies { ... } blocks (which run after
        // the plugin is applied) have populated SETTINGS_*_DATA.
        settings.gradle.settingsEvaluated {
            val delegate = SettingsPropertyDelegate(settings)
            settings.pluginManagement.repositories.apply(delegate, SETTINGS_PLUGIN_DATA)
            settings.dependencyResolutionManagement.repositories.apply(delegate, SETTINGS_DEPENDENCY_DATA)
        }
    }

    private fun RepositoryHandler.apply(
        propertyDelegate: PropertyDelegate,
        configuration: PrivateRepositoryConfiguration
    ) {
        var emptyUsername = false
        configuration.repositories.forEach { repository ->
            val uriProvider = repository.uriProvider
            val credentialsProvider = repository.credentialProvider

            logger?.debug("Getting uri for repository, provider = {}", uriProvider::class.simpleName)
            val uri = uriProvider.getUri(propertyDelegate)

            logger?.debug("Getting credentials for repository {}, provider = {}", uri, credentialsProvider::class.simpleName)
            val repositoryCredentials = credentialsProvider.getCredentials(propertyDelegate)
            if (repositoryCredentials == null && credentialsProvider !is PackageRepositoryCredentialProvider.NoOp) {
                logger?.error("Credentials could not be resolved from credential provider: ${credentialsProvider::class.simpleName}")
            }

            maven { maven ->
                maven.url = uri

                if (repositoryCredentials != null) {
                    emptyUsername = emptyUsername || repositoryCredentials.username.isNullOrBlank()
                    maven.credentials { credentials ->
                        credentials.username = repositoryCredentials.username
                        credentials.password = repositoryCredentials.token
                    }
                }
            }
            logger?.debug("Registered new maven dependency: {}", repository.uriProvider)
        }
        if (emptyUsername) {
            logger?.warn("Detected usage of unset or empty package username for at least one repository. This may" +
                    " result in an authorization issue depending on the token used.")
        }
        logger?.info("Configured ${configuration.repositories.size} private repositories")
    }


    private class ProjectPropertyDelegate(private val project: Project) : PropertyDelegate {
        override fun resolve(name: String): Any? {
            return project.findProperty(name)
        }
    }

    private class SettingsPropertyDelegate(settings: Settings) : PropertyDelegate {
        private val providers = settings.providers
        override fun resolve(name: String): Any? {
            return providers.gradleProperty(name)
                .orElse { providers.systemProperty(name) }
                .orElse { providers.environmentVariable(name) }
                .orNull
        }

        private fun <T> Provider<T>.orElse(`else`: () -> Provider<T>) : Provider<T> {
            if (isPresent) return this
            return orElse(`else`)
        }
    }
}
