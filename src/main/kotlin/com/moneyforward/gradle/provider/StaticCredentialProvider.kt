package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PropertyDelegate

/**
 * A basic, static [PackageRepositoryCredentialProvider] which uses and returns hardcoded credentials.
 * @param username the repository username to return in credentials
 * @param token the repository token to return in credentials
 */
class StaticCredentialProvider(
    private val username: String,
    private val token: String,
) : PackageRepositoryCredentialProvider {
    override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials {
        return PackageRepositoryCredentials(username, token)
    }
}
