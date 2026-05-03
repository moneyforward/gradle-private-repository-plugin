package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PropertyDelegate
import com.moneyforward.gradle.exception.EnvironmentException
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
    override fun getUri(propertyDelegate: PropertyDelegate): URI {
        return try {
            URI(System.getenv(uriEnvironmentVar)!!)
        } catch (e: URISyntaxException) {
            throw EnvironmentException(
                "Failed to resolve '$uriEnvironmentVar' as a valid URI. Please check your environment.",
                e,
            )
        } catch (e: NullPointerException) {
            throw EnvironmentException(
                "Environment variable '$uriEnvironmentVar' was not set. Please check your environment.",
                e,
            )
        }
    }
}
