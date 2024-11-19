package com.moneyforward.gradle

/**
 * A basic, static [GitHubCredentialProvider] which uses and returns hardcoded credentials.
 * @param username the GitHub username to return in credentials
 * @param token the GitHub token to return in credentials
 */
class StaticCredentialProvider(
    private val username: String,
    private val token: String
) : GitHubCredentialProvider {
    override fun getCredentials(propertyDelegate: PropertyDelegate): GitHubRepositoryCredentials {
        return GitHubRepositoryCredentials(username, token)
    }
}