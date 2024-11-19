package com.moneyforward.gradle

class ProjectPrivateRepositoryConfiguration : PrivateRepositoryConfiguration() {
    /**
     * If false the plugin will throw an exception if it finds empty (or null) credentials. Defaults to false.
     * The `com.moneyforward.allow-empty-credentials` property takes precedence over this value.
     */
    var allowEmptyCredentials: Boolean = false
}