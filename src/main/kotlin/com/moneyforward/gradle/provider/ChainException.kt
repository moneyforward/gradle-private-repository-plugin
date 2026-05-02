package com.moneyforward.gradle.provider

import aws.sdk.kotlin.runtime.auth.credentials.ProviderConfigurationException
import aws.sdk.kotlin.runtime.auth.credentials.SsoCredentialsProvider

class ChainException(message: String, cause: Exception? = null) : Exception(message, cause) {
    companion object {
        fun withAdvice(message: String, causes: List<Throwable>): ChainException {
            val suppressed = causes + causes.flatMap { cause -> cause.suppressed.asSequence() }
            val advice = suppressed
                .mapNotNull { ex -> getAdvice(ex)?.let { "- $it" } }
                .ifEmpty { null }
                ?.joinToString("\n", prefix = "\nAdvice (results may depend on your setup):\n")

            val ex = ChainException(message = message + (advice ?: ""))
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
            val awsProviderClass = ex.stackTrace.firstOrNull()?.className

            // SSO issue
            return when (awsProviderClass) {
                SsoCredentialsProvider::class.qualifiedName -> {
                    "Your SSO session has expired. To refresh this SSO session run `aws sso login` with the corresponding profile."
                }

                "aws.sdk.kotlin.runtime.auth.credentials.SsoTokenProviderKt" -> {
                    "Invalid or missing SSO session cache. Run `aws sso login` to initiate a new SSO session"
                }
                // Perhaps inspect other errors such as origin from StsWebIdentityCredentialsProvider
                else -> null
            }
        }
    }
}