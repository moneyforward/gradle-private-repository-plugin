package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PropertyDelegate
import com.moneyforward.gradle.exception.EnvironmentException

/**
 * A [PackageRepositoryCredentialProvider] that reads credentials from environment variables.
 *
 * @param usernameEnvironmentVar The environment variable name for the repository username.
 * @param tokenEnvironmentVar The environment variable name for the repository token.
 * @throws NullPointerException if the token environment variable is not set.
 */
class EnvironmentCredentialProvider(
    var usernameEnvironmentVar: String = DEFAULT_USERNAME_VAR,
    var tokenEnvironmentVar: String = DEFAULT_TOKEN_VAR,
) : PackageRepositoryCredentialProvider {
    companion object {
        const val DEFAULT_USERNAME_VAR = "GRADLE_PRIVATE_REPO_USERNAME"
        const val DEFAULT_TOKEN_VAR = "GRADLE_PRIVATE_REPO_TOKEN"
    }

    override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials {
        val token = System.getenv(tokenEnvironmentVar)
            ?: throw EnvironmentException(
                "Environment variable '$tokenEnvironmentVar' was not set. Please check your environment.",
            )

        return PackageRepositoryCredentials(
            username = System.getenv(usernameEnvironmentVar),
            token = token,
        )
    }
}
