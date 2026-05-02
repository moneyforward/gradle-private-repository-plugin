package com.moneyforward.gradle

import com.moneyforward.gradle.provider.PackageRepositoryCredentialProvider
import com.moneyforward.gradle.provider.PackageRepositoryUriProvider

/**
 * A GitHub repository definition containing a URL and [credentials provider][com.moneyforward.gradle.provider.PackageRepositoryCredentialProvider]
 */
open class DefaultPackageRepository(
    override var uriProvider: PackageRepositoryUriProvider,
    override var credentialProvider: PackageRepositoryCredentialProvider
) : PackageRepository
