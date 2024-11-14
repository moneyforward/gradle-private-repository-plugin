package io.github.evancmfw.gradle

/**
 * A data class containing a GitHub username and GitHub PAT token, used for verifying identity to GitHub packages
 */
data class GitHubRepositoryCredentials(
    val username: String,
    val token: String
)
