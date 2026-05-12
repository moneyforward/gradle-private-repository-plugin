package com.moneyforward.gradle.codeartifact

import com.moneyforward.gradle.PackageRepository
import com.moneyforward.gradle.provider.EnvironmentCredentialProvider
import com.moneyforward.gradle.provider.EnvironmentUriProvider
import com.moneyforward.gradle.provider.PackageRepositoryCredentialProvider
import com.moneyforward.gradle.provider.PackageRepositoryUriProvider
import com.moneyforward.gradle.provider.gradlePropertiesProvider
import com.moneyforward.gradle.provider.gradlePropertiesUriProvider
import com.moneyforward.gradle.provider.providersOf

/**
 * [PackageRepository] implementation for AWS CodeArtifact Maven repositories.
 *
 * @property uriProvider Resolves the repository endpoint URL from [CodeArtifactDetails].
 * @property credentialProvider Fetches short-lived CodeArtifact authorization tokens.
 */
open class CodeArtifactRepository : PackageRepository {
    override lateinit var uriProvider: PackageRepositoryUriProvider
    override lateinit var credentialProvider: PackageRepositoryCredentialProvider

    constructor()

    constructor(
        uriProvider: PackageRepositoryUriProvider,
        credentialProvider: PackageRepositoryCredentialProvider,
    ) {
        this.uriProvider = uriProvider
        this.credentialProvider = credentialProvider
    }

    class Default(
        details: CodeArtifactDetails,
        username: String? = null,
    ) : CodeArtifactRepository() {
        val uriEnvProvider: EnvironmentUriProvider = EnvironmentUriProvider()
        val credentialEnvProvider: EnvironmentCredentialProvider = EnvironmentCredentialProvider()

        override var uriProvider: PackageRepositoryUriProvider = providersOf(
            CodeArtifactUriProvider(details),
            uriEnvProvider,
            gradlePropertiesUriProvider(),
        )
        override var credentialProvider: PackageRepositoryCredentialProvider = providersOf(
            CodeArtifactCredentialProvider(details, username),
            credentialEnvProvider,
            gradlePropertiesProvider(),
        )

        var uriEnvironmentVar: String
            get() = uriEnvProvider.uriEnvironmentVar
            set(value) {
                uriEnvProvider.uriEnvironmentVar = value
            }

        var usernameEnvironmentVar: String
            get() = credentialEnvProvider.usernameEnvironmentVar
            set(value) {
                credentialEnvProvider.usernameEnvironmentVar = value
            }

        var tokenEnvironmentVar: String
            get() = credentialEnvProvider.tokenEnvironmentVar
            set(value) {
                credentialEnvProvider.tokenEnvironmentVar = value
            }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CodeArtifactRepository

        if (uriProvider != other.uriProvider) return false
        if (credentialProvider != other.credentialProvider) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uriProvider.hashCode()
        result = 31 * result + credentialProvider.hashCode()
        return result
    }

}
