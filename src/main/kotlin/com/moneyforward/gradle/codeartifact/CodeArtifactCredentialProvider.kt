package com.moneyforward.gradle.codeartifact

import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PropertyDelegate
import com.moneyforward.gradle.provider.PackageRepositoryCredentialProvider

class CodeArtifactCredentialProvider(
    private val details: CodeArtifactDetails,
    private val username: String? = null,
) : PackageRepositoryCredentialProvider {
    companion object {
        const val DEFAULT_USERNAME = "CodeArtifact"
    }

    override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials {
        val token = CodeArtifactClient
            .get(details)
            .getAuthorizationToken { req ->
                req.domain(details.domain)
                details.domainOwner?.let { req.domainOwner(it) }
            }.authorizationToken() ?: throw CodeArtifactException("Token response did not contain an authorization token")

        return PackageRepositoryCredentials(
            username = username ?: DEFAULT_USERNAME,
            token = token,
        )
    }
}
