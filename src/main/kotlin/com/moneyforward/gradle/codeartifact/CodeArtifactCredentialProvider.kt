package com.moneyforward.gradle.codeartifact

import aws.sdk.kotlin.services.codeartifact.getAuthorizationToken
import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PropertyDelegate
import com.moneyforward.gradle.provider.PackageRepositoryCredentialProvider
import kotlinx.coroutines.runBlocking

/**
 * [com.moneyforward.gradle.provider.PackageRepositoryCredentialProvider] that authenticates against AWS CodeArtifact by fetching
 * a short-lived authorization token via the CodeArtifact API.
 *
 * @param details Connection details identifying the domain and owner account.
 * @param username The Maven username to pair with the fetched token.
 */
class CodeArtifactCredentialProvider(
    private val details: CodeArtifactDetails,
    private val username: String? = null,
) : PackageRepositoryCredentialProvider {
    companion object {
        const val DEFAULT_USERNAME = "CodeArtifact"
    }

    /**
     * Fetches a CodeArtifact authorization token and returns it as [PackageRepositoryCredentials].
     *
     * @throws CodeArtifactException if the API response does not include a token.
     */
    override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials {
        val token = runBlocking {
            CodeArtifactClient.get(details).getAuthorizationToken {
                domain = details.domain
                domainOwner = details.domainOwner
            }
        }.authorizationToken ?: throw CodeArtifactException("Token response did not contain an authorization token")

        return PackageRepositoryCredentials(
            username = username ?: DEFAULT_USERNAME,
            token = token,
        )
    }
}
