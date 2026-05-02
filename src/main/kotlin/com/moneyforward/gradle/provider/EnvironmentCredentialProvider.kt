package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PropertyDelegate

/**
 * A [PackageRepositoryCredentialProvider] that reads credentials from environment variables.
 *
 * @param usernameEnvironmentVar The environment variable name for the repository username.
 * @param tokenEnvironmentVar The environment variable name for the repository token.
 * @throws NullPointerException if the token environment variable is not set.
 */
class EnvironmentCredentialProvider(
    var usernameEnvironmentVar: String = "GRADLE_PRIVATE_REPO_USERNAME",
    var tokenEnvironmentVar: String = "GRADLE_PRIVATE_REPO_TOKEN",
) : PackageRepositoryCredentialProvider {
    override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials {
        val token = System.getenv(tokenEnvironmentVar)
            ?: throw NullPointerException("Could not resolve token environment variable")

        return PackageRepositoryCredentials(
            username = System.getenv(usernameEnvironmentVar),
            token = token
        )
    }
}