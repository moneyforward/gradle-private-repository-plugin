package com.moneyforward.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

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
class PrivateRepositoryPlugin : Plugin<Project> {
    companion object {
        internal val PLUGIN_DATA = PrivateRepositoryConfiguration()
        internal const val USERNAME_PROPERTY = "moneyforward.github.username"
        internal const val TOKEN_PROPERTY = "moneyforward.github.token"
    }

    override fun apply(project: Project) {
        val logger = project.logger

        project.tasks.create("storeGitHubCredentials", StoreGitHubCredentialsTask::class.java)

        project.afterEvaluate {
            PLUGIN_DATA.repositories.forEach { repository ->
                val githubCredentials = repository.credentialProvider.getCredentials(project)

                it.repositories.maven { maven ->
                    maven.url = repository.url
                    if (githubCredentials != null) {
                        maven.credentials { credentials ->
                            credentials.username = githubCredentials.username
                            credentials.password = githubCredentials.token
                        }
                    }
                }
                logger.debug("Registered new maven dependency: {}", repository.url)
            }
            logger.info("Configured ${PLUGIN_DATA.repositories.size} private repositories")
        }
    }
}
