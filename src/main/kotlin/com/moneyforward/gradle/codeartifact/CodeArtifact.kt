package com.moneyforward.gradle.codeartifact

import com.moneyforward.gradle.PrivateRepositoryConfiguration
import com.moneyforward.gradle.provider.EnvironmentVariableCredentialProvider
import com.moneyforward.gradle.provider.providersOf

internal const val DEFAULT_USERNAME = "CodeArtifact"

/**
 * Creates a [CodeArtifactRepository] from individual parameters.
 *
 * @param domain The AWS CodeArtifact domain name.
 * @param repository The repository name within the domain.
 * @param domainOwner The AWS account ID that owns the domain, or null to use the caller's account.
 * @param ssoProfile The AWS SSO profile name to use for authentication, or null to use the default credential chain.
 * @param username The Maven username sent with repository requests.
 * @return A configured [CodeArtifactRepository].
 */
fun codeArtifact(
    domain: String,
    repository: String,
    domainOwner: Long? = null,
    region: String? = null,
    ssoProfile: String? = null,
    username: String = DEFAULT_USERNAME,
) = codeArtifact(
    CodeArtifactDetails(
        domain = domain,
        repository = repository,
        domainOwner = domainOwner,
        region = region,
        ssoProfile = ssoProfile,
    ),
    username,
)

/**
 * Creates a [CodeArtifactRepository] from a [CodeArtifactDetails] instance.
 *
 * @param details The CodeArtifact connection details.
 * @param username The Maven username sent with repository requests.
 * @return A configured [CodeArtifactRepository].
 */
fun codeArtifact(
    details: CodeArtifactDetails,
    username: String = DEFAULT_USERNAME,
): CodeArtifactRepository {
    val uriProvider = CodeArtifactUriProvider(details)

    val credentialsProvider = providersOf(
        CodeArtifactCredentialProvider(details, username),
        EnvironmentVariableCredentialProvider()
    )

    return CodeArtifactRepository(
        uriProvider = uriProvider,
        credentialProvider = credentialsProvider,
    )
}

/**
 * Registers an AWS CodeArtifact Maven repository with this plugin configuration.
 *
 * @param domain The AWS CodeArtifact domain name.
 * @param repository The repository name within the domain.
 * @param domainOwner The AWS account ID that owns the domain, or null to use the caller's account.
 * @param ssoProfile The AWS SSO profile name to use for authentication, or null to use the default credential chain.
 * @param username The Maven username sent with repository requests.
 * @param configure Optional block to further configure the [CodeArtifactRepository].
 */
fun PrivateRepositoryConfiguration.codeArtifactRepository(
    domain: String,
    repository: String,
    domainOwner: Long? = null,
    region: String? = null,
    ssoProfile: String? = null,
    username: String = DEFAULT_USERNAME,
    configure: CodeArtifactRepository.() -> Unit = {}
) {
    repository(
        repository = codeArtifact(
            domain = domain,
            repository = repository,
            domainOwner = domainOwner,
            username = username,
            region = region,
            ssoProfile = ssoProfile,
        ),
        configure = configure
    )
}

/**
 * Registers an AWS CodeArtifact repository using a [CodeArtifactDetails] builder block.
 *
 * @param config Block to configure [CodeArtifactDetails].
 */
fun PrivateRepositoryConfiguration.codeArtifactRepository(config: CodeArtifactDetails.() -> Unit) {
    return codeArtifactRepository(config) {}
}

/**
 * Registers an AWS CodeArtifact repository using a [CodeArtifactDetails] builder block
 * with additional repository configuration.
 *
 * @param details Block to configure [CodeArtifactDetails].
 * @param configure Block to further configure the [CodeArtifactRepository].
 */
fun PrivateRepositoryConfiguration.codeArtifactRepository(
    details: CodeArtifactDetails.() -> Unit,
    configure: CodeArtifactRepository.() -> Unit
) {
    val details = CodeArtifactDetails().apply(details)
    repository(
        repository = codeArtifact(
            details = details,
        ),
        configure = configure
    )
}
