package com.moneyforward.gradle

/**
 * A private-repository credentials entry, containing the username and token property names and the
 * resolved username and tokens
 */
data class StoreRepositoryCredentialsEntry(
    var usernameProperty: String = PrivateRepositoryPlugin.USERNAME_PROPERTY,
    var tokenProperty: String = PrivateRepositoryPlugin.TOKEN_PROPERTY,
    var username: String? = null,
    var token: String? = null,
)
