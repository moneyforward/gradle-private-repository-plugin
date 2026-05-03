package com.moneyforward.gradle.provider

import com.moneyforward.gradle.PackageRepositoryCredentials
import com.moneyforward.gradle.PropertyDelegate

/**
 * An interface which provides a set of [com.moneyforward.gradle.PackageRepositoryCredentials]
 * @see PropertyPackageCredentialProvider
 * @see StaticCredentialProvider
 */
fun interface PackageRepositoryCredentialProvider {
    /**
     * Returns a nullable set of [com.moneyforward.gradle.PackageRepositoryCredentials]. If null, the dependency resolution
     * may continue without attempting to download any related dependencies. This is primarily intended to be
     * used with CI/CD cached builds as credentials should not be required.
     */
    fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials?

    /**
     * A NoOp [PackageRepositoryCredentials] which always return a null set of credentials
     */
    object NoOp : PackageRepositoryCredentialProvider {
        override fun getCredentials(propertyDelegate: PropertyDelegate): PackageRepositoryCredentials? = null
    }
}
