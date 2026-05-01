package com.moneyforward.gradle

import com.moneyforward.gradle.provider.PackageCredentialProvider
import com.moneyforward.gradle.provider.PackageRepositoryUriProvider
import com.moneyforward.gradle.provider.StaticUriProvider
import java.net.URI

/**
 * Represents a private Maven package repository, pairing a URI provider with a credential provider.
 *
 * @property uriProvider Resolves the repository endpoint URL at configuration time.
 * @property credentialProvider Supplies the credentials required to access the repository.
 */
interface PackageRepository {
    val uriProvider: PackageRepositoryUriProvider
    val credentialProvider: PackageCredentialProvider

    companion object {
        /**
         * Creates a [PackageRepository] backed by [DefaultPackageRepository].
         *
         * @param uriProvider Resolves the repository endpoint URL.
         * @param credentialProvider Supplies repository credentials.
         */
        operator fun invoke(uriProvider: PackageRepositoryUriProvider, credentialProvider: PackageCredentialProvider): PackageRepository {
            return DefaultPackageRepository(uriProvider, credentialProvider)
        }

        /**
         * Creates a [DefaultPackageRepository] from a URI string and a credential provider.
         *
         * @param uri The repository endpoint URL as a string.
         * @param credentialProvider Supplies repository credentials.
         */
        fun create(uri: String, credentialProvider: PackageCredentialProvider): DefaultPackageRepository {
            return create(URI(uri), credentialProvider)
        }

        /**
         * Creates a [DefaultPackageRepository] from a [URI] and a credential provider.
         *
         * @param uri The repository endpoint URL.
         * @param credentialProvider Supplies repository credentials.
         */
        fun create(uri: URI, credentialProvider: PackageCredentialProvider): DefaultPackageRepository {
            return DefaultPackageRepository(
                uriProvider = StaticUriProvider(uri),
                credentialProvider = credentialProvider
            )
        }
    }
}