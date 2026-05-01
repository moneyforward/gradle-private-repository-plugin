package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PrivateRepositoryPlugin
import com.moneyforward.gradle.PropertyDelegate

/**
 * Configures and returns a [PropertyPackageCredentialProvider]
 * @param usernameProperty the property to use for resolving the package repository username used in the credentials
 * @param tokenProperty the property to use for resolving the package repository token used in the credentials
 * @return a configured [PropertyPackageCredentialProvider] using the given credentials
 */
fun gradlePropertiesProvider(
    usernameProperty: String = PrivateRepositoryPlugin.USERNAME_PROPERTY,
    tokenProperty: String = PrivateRepositoryPlugin.TOKEN_PROPERTY
): PropertyPackageCredentialProvider {
    return PropertyPackageCredentialProvider.propertyProviders.getOrPut(usernameProperty to tokenProperty) {
        PropertyPackageCredentialProvider(usernameProperty, tokenProperty)
    }
}

/**
 * An implementation of [PackageCredentialProvider] that returns [package-repository credentials][com.moneyforward.gradle.PackageRepositoryCredentials]
 * based on the gradle project settings (usually configured via various gradle.properties)
 *
 * @param usernameProperty the property to use for resolving the package repository username used in the credentials
 * @param tokenProperty the property to use for resolving the pcakage repository token used in the credentials
 */
open class PropertyPackageCredentialProvider internal constructor(
    private val usernameProperty: String,
    private val tokenProperty: String
) : PackageCredentialProvider {
    companion object {
        internal val propertyProviders = mutableMapOf<Pair<String, String>, PropertyPackageCredentialProvider>()
    }

    override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials? {
        try {
            return PackageRepositoryCredentials(
                propertyDelegate.resolveTo<String>(usernameProperty) ?: "",
                requireNotNullOrEmpty(propertyDelegate.resolveTo(tokenProperty))
            )
        } catch (ex: IllegalArgumentException) {
            val allowEmptyCredentials = propertyDelegate.resolveTo<String?>("com.moneyforward.allow-empty-credentials")

            if (allowEmptyCredentials?.toBoolean() ?: PrivateRepositoryPlugin.PROJECT_PLUGIN_DATA.allowEmptyCredentials) {
                return null
            }

            throw NullPointerException("Could not find package repositroy credentials. If building locally please configure " +
                    "and run the `storeRepositoryCredentials` task or check the following properties:" +
                    "\n\t- $usernameProperty\n\t- $tokenProperty")
        }
    }

    private fun requireNotNullOrEmpty(string: String?): String {
        if (string.isNullOrEmpty()) throw IllegalArgumentException("String cannot be null or empty")
        return string
    }
}