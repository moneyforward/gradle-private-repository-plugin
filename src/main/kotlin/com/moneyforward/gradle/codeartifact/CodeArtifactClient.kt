package com.moneyforward.gradle.codeartifact

import aws.sdk.kotlin.runtime.auth.credentials.ProfileCredentialsProvider
import aws.sdk.kotlin.services.codeartifact.CodeartifactClient
import kotlinx.coroutines.runBlocking

/**
 * Singleton that manages a shared [CodeartifactClient] instance for the build lifetime.
 */
object CodeArtifactClient {
    private var client: CodeartifactClient? = null

    /**
     * Returns the shared [CodeartifactClient], creating it if it does not yet exist.
     *
     * @param details Connection details used to configure credentials on first call.
     */
    @Synchronized
    fun get(details: CodeArtifactDetails): CodeartifactClient {
        if (client != null) return client!!
        return runBlocking { createClient(details) }
    }

    /**
     * Creates a [CodeartifactClient] using the credential strategy indicated by [details].
     *
     * Uses a named SSO profile when [CodeArtifactDetails.ssoProfile] is set,
     * otherwise falls back to the default AWS credential chain.
     */
    private suspend fun createClient(details: CodeArtifactDetails): CodeartifactClient {
        val profile = details.ssoProfile
        client = if (profile == null) {
            CodeartifactClient.fromEnvironment()
        } else {
            CodeartifactClient {
                credentialsProvider = ProfileCredentialsProvider(profile, region = details.region)
                region = details.region
            }
        }
        return client!!
    }
}
