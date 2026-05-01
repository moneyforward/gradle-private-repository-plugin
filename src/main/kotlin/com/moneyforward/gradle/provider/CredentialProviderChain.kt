package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PropertyDelegate
import org.gradle.api.logging.Logging

/**
 * A [PackageCredentialProvider] that tries each provider in order, returning the first non-null result.
 *
 * Providers are tried sequentially on each call. Once a provider has been tried, subsequent calls
 * advance to the next provider. Use [providersOf] to construct an instance.
 *
 * @param providers The ordered list of credential providers to try.
 */
class CredentialProviderChain(
    private val providers: List<PackageCredentialProvider>
) : PackageCredentialProvider {
    private val logger = Logging.getLogger(this::class.simpleName!!)

    override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials? {
        for (provider in providers) {
            try {
                return provider.getCredentials(propertyDelegate)
            } catch (e: Exception) {
                logger.error("Error getting package credentials from provider '${provider::class.simpleName}'", e)
            }
        }
        return null
    }
}

/**
 * Creates a [CredentialProviderChain] from the given providers, tried in order.
 *
 * @param providers The credential providers to chain, in priority order.
 * @return A [CredentialProviderChain] wrapping the given providers.
 */
fun providersOf(
    vararg providers: PackageCredentialProvider,
): CredentialProviderChain {
    return CredentialProviderChain(providers.toList())
}