package com.moneyforward.gradle.exception

import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.services.codeartifact.model.AccessDeniedException

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
            val advice =
                suppressed
                    .mapNotNull { ex -> getAdvice(ex)?.let { "- $it" } }
                    .ifEmpty { null }
                    ?.joinToString("\n", prefix = "\nAdvice (results may depend on your setup):\n")

            val ex = ChainException(message = message + (advice ?: ""))
            causes.forEach { ex.addSuppressed(it) }
            return ex
        }

        private fun getAdvice(ex: Throwable): String? =
            when (ex) {
                is AdviceException -> ex.getAdvice()
                is SdkClientException -> getAdvice(ex)
                is AccessDeniedException -> "Access was denied from repository, this is likely due to a misconfigured AWS profile or missing permissions."
                else -> null
            }

        // AwsCredentialsProviderChain concatenates individual provider failure messages into the
        // top-level exception message rather than adding them as suppressed exceptions, so we walk
        // the full tree and match on message content instead of exception type or stack frames.
        private fun getAdvice(ex: SdkClientException): String? =
            exceptionTree(ex).firstNotNullOfOrNull { t ->
                val msg = t.message ?: return@firstNotNullOfOrNull null
                when {
                    msg.contains("could not find source profile", ignoreCase = true) ->
                        msg.replaceFirstChar { it.uppercaseChar() } +
                            ". Run `aws configure sso --profile [PROFILE_NAME]` to setup the correct profile."

                    msg.contains("expired", ignoreCase = true) ->
                        "Your SSO session has expired. Run `aws sso login` with the corresponding profile."

                    msg.contains("token", ignoreCase = true) || msg.contains("cache", ignoreCase = true) ->
                        "Invalid or missing SSO session cache. Run `aws sso login` to initiate a new SSO session."

                    else -> null
                }
            }

        private fun exceptionTree(ex: Throwable): Sequence<Throwable> =
            sequence {
                yield(ex)
                ex.suppressed.forEach { yieldAll(exceptionTree(it)) }
                ex.cause?.let { yieldAll(exceptionTree(it)) }
            }
    }
}
