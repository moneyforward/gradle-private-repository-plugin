package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PropertyDelegate
import java.net.URI

/**
 * A [PackageRepositoryUriProvider] that always returns the same fixed [URI].
 *
 * @param uri The repository endpoint URL to return on every call.
 */
class StaticUriProvider(
    private val uri: URI,
) : PackageRepositoryUriProvider {
    override fun getUri(propertyDelegate: PropertyDelegate): URI {
        return uri
    }
}
