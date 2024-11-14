package com.moneyforward.gradle

data class StoreGitHubCredentialsEntry(
    var usernameProperty: String = PrivateRepositoryPlugin.USERNAME_PROPERTY,
    var tokenProperty: String = PrivateRepositoryPlugin.TOKEN_PROPERTY,
    var username: String? = null,
    var token: String? = null
)