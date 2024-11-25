package com.moneyforward.gradle

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
 * The main plugin class for `com.moneyforward.private-repository-plugin`
 * This class automatically configures gradle repositories to include dependencies to private
 * packages.
 */
class PrivateRepositoryPlugin : Plugin<Any> {
    companion object {
        internal val PROJECT_PLUGIN_DATA = ProjectPrivateRepositoryConfiguration()
        internal val SETTINGS_PLUGIN_DATA = PrivateRepositoryConfiguration()
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

        project.tasks.create(StoreGitHubCredentialsTask.NAME, StoreGitHubCredentialsTask::class.java)

        project.afterEvaluate {
            val tasks = it.gradle.startParameter.taskNames
            // do not apply repository settings if running the storeGitHubCredentials task
            if (tasks.any { task -> task == StoreGitHubCredentialsTask.NAME }) {
                project.repositories.apply(ProjectPropertyDelegate(project), PROJECT_PLUGIN_DATA)
                if (tasks.size > 1) logger.warn(
                    "{} should be ran in isolation, running it with other tasks may cause unexpected issues.",
                    StoreGitHubCredentialsTask.NAME
                )
            }
        }
    }

    private fun apply(settings: Settings) {
        settings.pluginManagement.repositories.apply(SettingsPropertyDelegate(settings), SETTINGS_PLUGIN_DATA)
    }

    private fun RepositoryHandler.apply(
        propertyDelegate: PropertyDelegate,
        configuration: PrivateRepositoryConfiguration
    ) {
        var emptyUsername = false
        configuration.repositories.forEach { repository ->
            logger?.debug("Getting credentials for repository {}, provider = {}",
                repository.url, repository.credentialProvider::class.simpleName)
            val githubCredentials = repository.credentialProvider.getCredentials(propertyDelegate)

            maven { maven ->
                maven.url = repository.url
                if (githubCredentials != null) {
                    emptyUsername = emptyUsername || githubCredentials.username.isBlank()
                    maven.credentials { credentials ->
                        credentials.username = githubCredentials.username
                        credentials.password = githubCredentials.token
                    }
                }
            }
            logger?.debug("Registered new maven dependency: {}", repository.url)
        }
        if (emptyUsername) {
            logger?.warn("Detected usage of unset or empty GitHub username for at least one repository. This may" +
                    " result in an authorization issue depending on the token used.")
        }
        logger?.info("Configured ${PROJECT_PLUGIN_DATA.repositories.size} private repositories")
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
