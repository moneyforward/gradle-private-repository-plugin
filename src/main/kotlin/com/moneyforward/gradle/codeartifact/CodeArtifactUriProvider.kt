package com.moneyforward.gradle.codeartifact

import aws.sdk.kotlin.services.codeartifact.getRepositoryEndpoint
import aws.sdk.kotlin.services.codeartifact.model.PackageFormat
import com.moneyforward.gradle.PropertyDelegate
import com.moneyforward.gradle.provider.PackageRepositoryUriProvider
import kotlinx.coroutines.runBlocking
import java.net.URI

/**
 * [com.moneyforward.gradle.provider.PackageRepositoryUriProvider] that resolves the Maven endpoint URL for an AWS CodeArtifact repository.
 *
 * @property details Connection details identifying the domain, owner, and repository.
 */
class CodeArtifactUriProvider(
    val details: CodeArtifactDetails,
) : PackageRepositoryUriProvider {
    /**
     * Fetches the repository endpoint from the CodeArtifact API and returns it as a [URI].
     *
     * @throws CodeArtifactException if the API response does not include an endpoint, or if the
     * returned URL cannot be parsed as a valid URI.
     */
    override fun getUri(propertyDelegate: PropertyDelegate): URI {
        val endpoint = runBlocking {
            CodeArtifactClient.get(details).getRepositoryEndpoint {
                domain = details.domain
                domainOwner = details.domainOwner
                repository = details.repository
                format = PackageFormat.Maven
            }
        }.repositoryEndpoint ?: throw CodeArtifactException("Repository endpoint response did not contain an endpoint")

        return try {
            URI(endpoint)
        } catch (e: Exception) {
            throw CodeArtifactException("Could not create URI from endpoint url", e)
        }
    }
}
