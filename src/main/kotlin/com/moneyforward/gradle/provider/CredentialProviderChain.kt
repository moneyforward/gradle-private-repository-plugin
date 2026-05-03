package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PropertyDelegate
import com.moneyforward.gradle.exception.ChainException

/**
 * A [PackageRepositoryCredentialProvider] that tries each provider in order, returning the first non-null result.
 *
 * Providers are tried sequentially on each call. Once a provider has been tried, subsequent calls
 * advance to the next provider. Use [providersOf] to construct an instance.
 *
 * @param providers The ordered list of credential providers to try.
 */
open class CredentialProviderChain(
    private val providers: List<PackageRepositoryCredentialProvider>,
) : PackageRepositoryCredentialProvider {
    private val failures = mutableListOf<Exception>()

    override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials {
        for (provider in providers) {
            try {
                return provider.getCredentials(propertyDelegate)!!
            } catch (e: Exception) {
                failures.add(e)
            }
        }

        val providerString = providers.joinToString(separator = " -> ") { it::class.simpleName!! }
        throw ChainException.withAdvice(
            "Failed to resolve credentials from all chain providers ($providerString)",
            failures,
        )
    }
}

/**
 * Creates a [CredentialProviderChain] from the given providers, tried in order.
 *
 * @param providers The credential providers to chain, in priority order.
 * @return A [CredentialProviderChain] wrapping the given providers.
 */
fun providersOf(vararg providers: PackageRepositoryCredentialProvider): CredentialProviderChain {
    return CredentialProviderChain(providers.toList())
}
