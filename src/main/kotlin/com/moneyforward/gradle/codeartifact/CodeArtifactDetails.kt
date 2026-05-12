package com.moneyforward.gradle.codeartifact

/**
 * Connection details for an AWS CodeArtifact Maven repository.
 *
 * @property domain The CodeArtifact domain name.
 * @property repository The repository name within the domain.
 * @property domainOwner The AWS account ID that owns the domain, or null to use the caller's account.
 * @property ssoProfile The AWS SSO profile name to use for authentication, or null to use the default credential chain.
 */
class CodeArtifactDetails {
    lateinit var domain: String
    lateinit var repository: String
    var domainOwner: String? = null
    var ssoProfile: String? = null
    var region: String? = null

    constructor()

    constructor(domain: String, repository: String, domainOwner: String? = null, region: String? = null, ssoProfile: String? = null) {
        this.domain = domain
        this.repository = repository
        this.domainOwner = domainOwner
        this.region = region
        this.ssoProfile = ssoProfile
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CodeArtifactDetails

        if (domain != other.domain) return false
        if (repository != other.repository) return false
        if (domainOwner != other.domainOwner) return false
        if (ssoProfile != other.ssoProfile) return false
        if (region != other.region) return false

        return true
    }

    override fun hashCode(): Int {
        var result = domain.hashCode()
        result = 31 * result + repository.hashCode()
        result = 31 * result + (domainOwner?.hashCode() ?: 0)
        result = 31 * result + (ssoProfile?.hashCode() ?: 0)
        result = 31 * result + (region?.hashCode() ?: 0)
        return result
    }
}
