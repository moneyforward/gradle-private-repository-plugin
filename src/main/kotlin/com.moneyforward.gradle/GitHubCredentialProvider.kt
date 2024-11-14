package com.moneyforward.gradle

import org.gradle.api.Project

/**
 * An interface which provides a set of [GitHubRepositoryCredentials]
 * @see PropertyGitHubCredentialProvider
 * @see StaticCredentialProvider
 */
interface GitHubCredentialProvider {
    /**
     * Returns a nullable set of [GitHubRepositoryCredentials]. If null, the dependency resolution
     * may continue without attempting to download any related dependencies. This is primarily intended to be
     * used with CI/CD cached builds as credentials should not be required.
     */
    fun getCredentials(project: Project): GitHubRepositoryCredentials?

    /**
     * A NoOp [GitHubRepositoryCredentials] which always return a null set of credentials
     */
    object NoOp : GitHubCredentialProvider {
        override fun getCredentials(project: Project): GitHubRepositoryCredentials? = null
    }
}