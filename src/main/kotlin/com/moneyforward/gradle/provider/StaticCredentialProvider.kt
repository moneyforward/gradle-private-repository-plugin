package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PropertyDelegate

/**
 * A basic, static [PackageCredentialProvider] which uses and returns hardcoded credentials.
 * @param username the GitHub username to return in credentials
 * @param token the GitHub token to return in credentials
 */
class StaticCredentialProvider(
    private val username: String,
    private val token: String
) : PackageCredentialProvider {
    override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials {
        return PackageRepositoryCredentials(username, token)
    }
}