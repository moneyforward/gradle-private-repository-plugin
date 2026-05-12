package com.moneyforward.gradle

import com.moneyforward.gradle.provider.PackageRepositoryCredentialProvider
import com.moneyforward.gradle.provider.PackageRepositoryUriProvider

/**
 * A Package repository definition containing a URL and [credentials provider][com.moneyforward.gradle.provider.PackageRepositoryCredentialProvider]
 */
open class DefaultPackageRepository(
    override var uriProvider: PackageRepositoryUriProvider,
    override var credentialProvider: PackageRepositoryCredentialProvider,
) : PackageRepository {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DefaultPackageRepository

        if (uriProvider != other.uriProvider) return false
        if (credentialProvider != other.credentialProvider) return false

        return true
    }

    override fun hashCode(): Int {
        var result = uriProvider.hashCode()
        result = 31 * result + credentialProvider.hashCode()
        return result
    }
}
