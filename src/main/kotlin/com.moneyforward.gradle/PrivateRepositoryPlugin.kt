package com.moneyforward.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.initialization.Settings

/**
 * Configures the [private repository configuration][PrivateRepositoryConfiguration] used
 * by the [private-repository-plugin][PrivateRepositoryPlugin].
 *
 * @see PrivateRepositoryConfiguration
 * @see PrivateRepositoryConfiguration.repository
 */
fun RepositoryHandler.private(configure: PrivateRepositoryConfiguration.() -> Unit) {
    configure(PrivateRepositoryPlugin.PLUGIN_DATA)
}

/**
 * The main plugin class for `com.moneyforward.private-repository-plugin`
 * This class automatically configures gradle repositories to include dependencies to private
 * packages.
 */
class PrivateRepositoryPlugin : AbstractPrivateRepositoryPlugin<Any>() {
    override val pluginData: PrivateRepositoryConfiguration get() = PLUGIN_DATA
    companion object {
        internal lateinit var PROJECT: Project
        internal val PLUGIN_DATA = PrivateRepositoryConfiguration()
        internal const val USERNAME_PROPERTY = "private-repository.github.username"
        internal const val TOKEN_PROPERTY = "private-repository.github.token"
    }

    override fun apply(target: Any) {
        if (target is Project) applyToProject(target)
        else if (target is Settings) applyToSettings(target)
        else throw IllegalArgumentException("target was neither Project or Settings")
    }

    private fun applyToProject(project: Project) {
        PROJECT = project

        project.tasks.create("storeGitHubCredentials", StoreGitHubCredentialsTask::class.java)

        project.afterEvaluate {
            it.repositories.applyRepositoryConfigurations(project::findProperty)
        }
    }

    private fun applyToSettings(settings: Settings) {

    }
}
