package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PropertyDelegate

/**
 * A [PackageCredentialProvider] that reads credentials from environment variables.
 *
 * @param usernameEnv The environment variable name for the repository username.
 * @param tokenEnv The environment variable name for the repository token.
 * @throws NullPointerException if the token environment variable is not set.
 */
class EnvironmentVariableCredentialProvider(
    private val usernameEnv: String = "GRADLE_PRIVATE_REPO_USERNAME",
    private val tokenEnv: String = "GRADLE_PRIVATE_REPO_TOKEN",
) : PackageCredentialProvider {
    override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials? {
        val token = System.getenv(tokenEnv)
            ?: throw NullPointerException("Could not resolve token environment variable")

        return PackageRepositoryCredentials(
            username = System.getProperty(usernameEnv),
            token = token
        )
    }
}