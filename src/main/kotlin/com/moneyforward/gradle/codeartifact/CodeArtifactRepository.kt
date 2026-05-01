package com.moneyforward.gradle.codeartifact

import com.moneyforward.gradle.PackageRepository
import com.moneyforward.gradle.provider.PackageCredentialProvider

/**
 * [PackageRepository] implementation for AWS CodeArtifact Maven repositories.
 *
 * @property uriProvider Resolves the repository endpoint URL from [CodeArtifactDetails].
 * @property credentialProvider Fetches short-lived CodeArtifact authorization tokens.
 */
class CodeArtifactRepository(
    override var uriProvider: CodeArtifactUriProvider,
    override var credentialProvider: PackageCredentialProvider
) : PackageRepository