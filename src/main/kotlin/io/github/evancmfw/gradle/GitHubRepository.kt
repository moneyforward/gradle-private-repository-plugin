package io.github.evancmfw.gradle

import java.net.URI

/**
 * A GitHub repository definition containing a URL and [credentials provider][GitHubCredentialProvider]
 */
data class GitHubRepository(var url: URI, var credentialProvider: GitHubCredentialProvider)