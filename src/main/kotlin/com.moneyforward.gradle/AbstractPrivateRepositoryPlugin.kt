package com.moneyforward.gradle

import org.gradle.api.Plugin
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.slf4j.Logger

abstract class AbstractPrivateRepositoryPlugin<T> : Plugin<T> {
    protected abstract val pluginData: PrivateRepositoryConfiguration
    protected var pLogger: Logger? = null

    protected fun RepositoryHandler.applyRepositoryConfigurations(propertyResolver: (String) -> Any?) {
        pluginData.repositories.forEach { repository ->
            pLogger?.debug("Getting credentials for repository {}, provider = {}",
                repository.url, repository.credentialProvider::class.simpleName)
            val githubCredentials = repository.credentialProvider.getCredentials(propertyResolver)
            maven { maven ->
                maven.url = repository.url
                if (githubCredentials != null) {
                    maven.credentials { credentials ->
                        credentials.username = githubCredentials.username
                        credentials.password = githubCredentials.token
                    }
                }
            }
            pLogger?.debug("Registered new maven dependency: {}", repository.url)
        }
        pLogger?.info("Configured ${pluginData.repositories.size} private repositories")
    }
}