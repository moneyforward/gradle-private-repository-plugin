package io.github.evancmfw.gradle

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
        internal const val USERNAME_PROPERTY = "private-repository.github.username"
        internal const val TOKEN_PROPERTY = "private-repository.github.token"
    }

    override fun apply(project: Project) {
        val logger = project.logger

        project.tasks.create("storeGitHubCredentials", StoreGitHubCredentialsTask::class.java)

        project.afterEvaluate {
            PLUGIN_DATA.repositories.forEach { repository ->
                logger.debug("Getting credentials for repository {}, provider = {}",
                    repository.url, repository.credentialProvider::class.simpleName)
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
