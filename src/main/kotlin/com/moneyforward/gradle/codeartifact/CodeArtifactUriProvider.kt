package com.moneyforward.gradle.codeartifact

import com.moneyforward.gradle.PropertyDelegate
import com.moneyforward.gradle.provider.PackageRepositoryUriProvider
import software.amazon.awssdk.services.codeartifact.model.PackageFormat
import java.net.URI

data class CodeArtifactUriProvider(
    val details: CodeArtifactDetails,
) : PackageRepositoryUriProvider {
    override fun getUri(propertyDelegate: PropertyDelegate): URI {
        val endpoint = CodeArtifactClient
            .get(details)
            .getRepositoryEndpoint { req ->
                req.domain(details.domain)
                details.domainOwner?.let { req.domainOwner(it) }
                req.repository(details.repository)
                req.format(PackageFormat.MAVEN)
            }.repositoryEndpoint() ?: throw CodeArtifactException("Repository endpoint response did not contain an endpoint")

        return try {
            URI(endpoint)
        } catch (e: Exception) {
            throw CodeArtifactException("Could not create URI from endpoint url", e)
        }
    }
}
