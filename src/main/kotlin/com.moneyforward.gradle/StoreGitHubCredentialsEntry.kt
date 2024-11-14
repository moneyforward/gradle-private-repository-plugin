package com.moneyforward.gradle

/**
 * A GitHub credentials entry, containing the username and token property names and the
 * resolved username and tokens
 */
data class StoreGitHubCredentialsEntry(
    var usernameProperty: String = PrivateRepositoryPlugin.USERNAME_PROPERTY,
    var tokenProperty: String = PrivateRepositoryPlugin.TOKEN_PROPERTY,
    var username: String? = null,
    var token: String? = null
)