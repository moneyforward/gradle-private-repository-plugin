package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PropertyDelegate
import com.moneyforward.gradle.exception.ChainException
import java.net.URI

/**
 * A [PackageRepositoryUriProvider] that tries each delegate in order, returning the first
 * successful result.
 *
 * If every provider throws, a [com.moneyforward.gradle.exception.ChainException] is raised that aggregates all failures.
 *
 * @param providers The URI providers to try, in priority order.
 */
class UriProviderChain(
    val providers: List<PackageRepositoryUriProvider>,
) : PackageRepositoryUriProvider {
    private val failures = mutableListOf<Exception>()

    override fun getUri(propertyDelegate: PropertyDelegate): URI {
        for (provider in providers) {
            try {
                return provider.getUri(propertyDelegate)
            } catch (e: Exception) {
                failures.add(e)
            }
        }

        val providerString = providers.joinToString(separator = " -> ") { it::class.simpleName!! }
        throw ChainException.withAdvice(
            "Failed to resolve URI from all chain providers ($providerString)",
            failures,
        )
    }
}

/**
 * Creates a [UriProviderChain] from the given providers, tried in order.
 *
 * @param providers The credential providers to chain, in priority order.
 * @return A [UriProviderChain] wrapping the given providers.
 */
fun providersOf(vararg providers: PackageRepositoryUriProvider): UriProviderChain {
    return UriProviderChain(providers.toList())
}
