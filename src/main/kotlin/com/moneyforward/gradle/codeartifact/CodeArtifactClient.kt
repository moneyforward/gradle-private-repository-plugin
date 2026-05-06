package com.moneyforward.gradle.codeartifact

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.codeartifact.CodeartifactClient as AwsCodeartifactClient

object CodeArtifactClient {
    private var client: AwsCodeartifactClient? = null

    @Synchronized
    fun get(details: CodeArtifactDetails): AwsCodeartifactClient {
        if (client != null) return client!!
        return createClient(details).also { client = it }
    }

    private fun createClient(details: CodeArtifactDetails): AwsCodeartifactClient {
        var credentialsProvider = DefaultCredentialsProvider.create()
        if (details.ssoProfile != null) {
            credentialsProvider = credentialsProvider.copy {
                it.profileName(details.ssoProfile)
            }
        }

        return AwsCodeartifactClient
            .builder()
            .apply { details.region?.let { region(Region.of(it)) } }
            .credentialsProvider(credentialsProvider)
            .build()
    }
}
