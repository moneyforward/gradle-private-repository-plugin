package com.moneyforward.gradle

import com.moneyforward.gradle.provider.PackageCredentialProvider
import com.moneyforward.gradle.provider.gradlePropertiesProvider
import java.net.URI

/**
 * Class containing and configuring a list of private [GitHub repositories][PackageRepository] to use for dependency resolution.
 * @see repository
 * @see repository
 */
open class PrivateRepositoryConfiguration {
    private var mutableRepositories = mutableSetOf<PackageRepository>()

    /**
     * The set of private [repositories][PackageRepository] to use in dependency resolution
     */
    var repositories: Set<PackageRepository> set(value) {
        mutableRepositories = value.toMutableSet()
    } get() = mutableRepositories

    /**
     * Adds and a new [PackageRepository] to the configuration
     * @param url the [URI] of the GitHub repository
     * @param credentialProvider the [com.moneyforward.gradle.provider.PackageCredentialProvider] to use for authentication with GitHub packages
     * @param configure the configuration block for the [PackageRepository]
     * @return a configured [PackageRepository]
     */
    fun repository(
        url: URI,
        credentialProvider: PackageCredentialProvider = gradlePropertiesProvider(),
        configure: PackageRepository.() -> Unit = {}
    ) {
        repository(PackageRepository.create(url, credentialProvider), configure)
    }

    /**
     * Adds and a new [PackageRepository] to the configuration
     * @param url the url of the GitHub repository
     * @param credentialProvider the [PackageCredentialProvider] to use for authentication with GitHub packages
     * @param configure the configuration block for the [PackageRepository]
     * @return a configured [PackageRepository]
     */
    fun repository(
        url: String,
        credentialProvider: PackageCredentialProvider = gradlePropertiesProvider(),
        configure: PackageRepository.() -> Unit = {}
    ) = repository(URI(url), credentialProvider, configure)

    fun <T : PackageRepository> repository(repository: T, configure: T.() -> Unit = {}) {
        mutableRepositories.add(repository.apply(configure))
    }
}

