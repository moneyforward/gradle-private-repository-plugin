package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PrivateRepositoryPlugin
import com.moneyforward.gradle.PropertyDelegate
import java.net.URI

fun gradlePropertiesUriProvider(urlProperty: String = PrivateRepositoryPlugin.URL_PROPERTY): PropertyUriProvider {
    return PropertyUriProvider(urlProperty)
}

class PropertyUriProvider internal constructor(
    private val uriProperty: String,
) : PackageRepositoryUriProvider {
    override fun getUri(propertyDelegate: PropertyDelegate): URI {
        try {
            val value = propertyDelegate.resolveTo<String>(uriProperty)
            return URI.create(value!!)
        } catch (e: IllegalArgumentException) {
            throw NullPointerException(
                "Could not find package repository URI. If building locally please configure " +
                    "and run the `storeRepositoryCredentials` task or check the following properties:" +
                    "\n\t- $uriProperty",
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PropertyUriProvider

        return uriProperty == other.uriProperty
    }

    override fun hashCode(): Int {
        return uriProperty.hashCode()
    }
}
