package io.github.evancmfw.gradle

import java.net.URI

/**
 * Class containing and configuring a list of private [GitHub repositories][GitHubRepository] to use for dependency resolution.
 * @see repository
 * @see repository
 */
open class PrivateRepositoryConfiguration {
    private var mutableRepositories = mutableSetOf<GitHubRepository>()

    /**
     * If false the plugin will throw an exception if it finds empty (or null) credentials. Defaults to false.
     * The `com.moneyforward.allow-empty-credentials` property takes precedence over this value.
     */
    var allowEmptyCredentials: Boolean = false

    /**
     * The set of private [repositories][GitHubRepository] to use in dependency resolution
     */
    var repositories: Set<GitHubRepository> set(value) {
        mutableRepositories = value.toMutableSet()
    } get() = mutableRepositories

    /**
     * Adds and a new [GitHubRepository] to the configuration
     * @param url the [URI] of the GitHub repository
     * @param credentialProvider the [GitHubCredentialProvider] to use for authentication with GitHub packages
     * @param configure the configuration block for the [GitHubRepository]
     * @return a configured [GitHubRepository]
     */
    fun repository(
        url: URI,
        credentialProvider: GitHubCredentialProvider = gradlePropertiesProvider(),
        configure: GitHubRepository.() -> Unit = {}
    ) {
        mutableRepositories.add(GitHubRepository(url, credentialProvider).apply(configure))
    }

    /**
     * Adds and a new [GitHubRepository] to the configuration
     * @param url the url of the GitHub repository
     * @param credentialProvider the [GitHubCredentialProvider] to use for authentication with GitHub packages
     * @param configure the configuration block for the [GitHubRepository]
     * @return a configured [GitHubRepository]
     */
    fun repository(
        url: String,
        credentialProvider: GitHubCredentialProvider = gradlePropertiesProvider(),
        configure: GitHubRepository.() -> Unit = {}
    ) = repository(URI(url), credentialProvider, configure)
}
