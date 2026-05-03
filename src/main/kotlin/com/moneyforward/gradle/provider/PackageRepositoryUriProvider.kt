package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PropertyDelegate
import java.net.URI

/**
 * Resolves the endpoint [URI] for a private Maven repository.
 *
 * @see StaticUriProvider
 */
fun interface PackageRepositoryUriProvider {
    /**
     * Returns the repository endpoint URI.
     *
     * @param propertyDelegate Provides access to Gradle project properties if needed for resolution.
     */
    fun getUri(propertyDelegate: PropertyDelegate): URI
}
