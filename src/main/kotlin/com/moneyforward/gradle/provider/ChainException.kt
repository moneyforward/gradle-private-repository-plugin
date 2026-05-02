package com.moneyforward.gradle.provider

import aws.sdk.kotlin.runtime.auth.credentials.ProviderConfigurationException
import aws.sdk.kotlin.runtime.auth.credentials.SsoCredentialsProvider
import com.moneyforward.gradle.codeartifact.CodeArtifactException
import kotlin.collections.ifEmpty
import kotlin.collections.joinToString

class ChainException(message: String, cause: Exception? = null) : Exception(message, cause) {
    companion object {
        fun withAdvice(message: String, causes: List<Throwable>): CodeArtifactException {
            val suppressed = causes + causes.flatMap { cause -> cause.suppressedExceptions }
            val advice = suppressed
                .mapNotNull { ex -> getAdvice(ex)?.let { "- $it" } }
                .ifEmpty { null }
                ?.joinToString("\n", prefix = "\nAdvice (results may depend on your setup):\n")

            val ex = CodeArtifactException(message + (advice ?: ""))
            causes.forEach { ex.addSuppressed(it) }
            return ex
        }

        private fun getAdvice(ex: Throwable): String? {
            return when (ex) {
                is ProviderConfigurationException -> getAdvice(ex)
                else -> null
            }
        }

        private fun getAdvice(ex: ProviderConfigurationException): String? {
            // SSO issue
            if (ex.stackTrace[0].className == SsoCredentialsProvider::class.qualifiedName) {
                return "Your SSO session has expired. To refresh this SSO session run `aws sso login` with the corresponding profile."
            }

            // Perhaps inspect other errors such as origin from StsWebIdentityCredentialsProvider
            return null
        }
    }
}