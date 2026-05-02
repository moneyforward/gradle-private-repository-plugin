package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PropertyDelegate
import org.gradle.api.logging.Logging
import java.net.URI
import java.net.URISyntaxException

/**
 * Resolves a repository URI from an environment variable.
 *
 * @param uriEnvironmentVar The name of the environment variable holding the repository URL.
 * Defaults to `GRADLE_PRIVATE_REPO_URL`.
 * @throws URISyntaxException if the environment variable value is not a valid URI.
 * @throws NullPointerException if the environment variable is not set.
 */
class EnvironmentUriProvider(
    var uriEnvironmentVar: String = "GRADLE_PRIVATE_REPO_URL",
) : PackageRepositoryUriProvider {
    private val logger = Logging.getLogger(this::class.simpleName!!)

    override fun getUri(propertyDelegate: PropertyDelegate): URI {
        return try {
            URI(System.getenv(uriEnvironmentVar)!!)
        } catch (e: URISyntaxException) {
            logger.error("Failed to resolve `${uriEnvironmentVar}` as a valid URI. Please check your environment.")
            throw e
        }
    }
}