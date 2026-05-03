package com.moneyforward.gradle.exception

import aws.sdk.kotlin.runtime.auth.credentials.ProviderConfigurationException
import aws.sdk.kotlin.runtime.auth.credentials.SsoCredentialsProvider
import aws.sdk.kotlin.services.codeartifact.model.AccessDeniedException
import org.gradle.configurationcache.extensions.capitalized

class ChainException(
    message: String,
    cause: Exception? = null,
) : Exception(message, cause) {
    companion object {
        fun withAdvice(
            message: String,
            causes: List<Throwable>,
        ): ChainException {
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
                is AdviceException -> {
                    ex.getAdvice()
                }

                is ProviderConfigurationException -> {
                    getAdvice(ex)
                }

                is AccessDeniedException -> {
                    "Access was denied from repository, this is likely due to a misconfigured AWS profile or missing permissions."
                }

                else -> {
                    null
                }
            }
        }

        private fun getAdvice(ex: ProviderConfigurationException): String? {
            val awsProviderClass = ex.stackTrace.firstOrNull()?.className

            if (ex.message?.contains("could not find source profile") == true) {
                return ex.message!!.capitalized() + ". Run `aws configure sso --profile [PROFILE_NAME]` to setup the correct profile."
            }

            // SSO issue
            return when (awsProviderClass) {
                SsoCredentialsProvider::class.qualifiedName -> {
                    "Your SSO session has expired. To refresh this SSO session run `aws sso login` with the corresponding profile."
                }

                "aws.sdk.kotlin.runtime.auth.credentials.SsoTokenProviderKt" -> {
                    "Invalid or missing SSO session cache. Run `aws sso login` to initiate a new SSO session"
                }

                // Perhaps inspect other errors such as origin from StsWebIdentityCredentialsProvider
                else -> {
                    null
                }
            }
        }
    }
}
