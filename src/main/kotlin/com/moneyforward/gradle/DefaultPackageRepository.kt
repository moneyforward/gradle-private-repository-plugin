package com.moneyforward.gradle

import com.moneyforward.gradle.provider.PackageCredentialProvider
import com.moneyforward.gradle.provider.PackageRepositoryUriProvider

/**
 * A GitHub repository definition containing a URL and [credentials provider][com.moneyforward.gradle.provider.PackageCredentialProvider]
 */
open class DefaultPackageRepository(
    override var uriProvider: PackageRepositoryUriProvider,
    override var credentialProvider: PackageCredentialProvider
) : PackageRepository
