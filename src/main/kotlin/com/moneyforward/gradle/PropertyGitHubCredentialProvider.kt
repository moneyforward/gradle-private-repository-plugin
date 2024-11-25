package com.moneyforward.gradle

/**
 * Configures and returns a [PropertyGitHubCredentialProvider]
 * @param usernameProperty the property to use for resolving the GitHub username used in the credentials
 * @param tokenProperty the property to use for resolving the GitHub token used in the credentials
 * @return a configured [PropertyGitHubCredentialProvider] using the given credentials
 */
fun gradlePropertiesProvider(
    usernameProperty: String = PrivateRepositoryPlugin.USERNAME_PROPERTY,
    tokenProperty: String = PrivateRepositoryPlugin.TOKEN_PROPERTY
): PropertyGitHubCredentialProvider {
    return PropertyGitHubCredentialProvider.propertyProviders.getOrPut(usernameProperty to tokenProperty) {
        PropertyGitHubCredentialProvider(usernameProperty, tokenProperty)
    }
}

/**
 * An implementation of [GitHubCredentialProvider] that returns [GitHub credentials][GitHubRepositoryCredentials]
 * based on the gradle project settings (usually configured via various gradle.properties)
 *
 * @param usernameProperty the property to use for resolving the GitHub username used in the credentials
 * @param tokenProperty the property to use for resolving the GitHub token used in the credentials
 */
open class PropertyGitHubCredentialProvider internal constructor(
    private val usernameProperty: String,
    private val tokenProperty: String
) : GitHubCredentialProvider {
    companion object {
        internal val propertyProviders = mutableMapOf<Pair<String, String>, PropertyGitHubCredentialProvider>()
    }

    override fun getCredentials(propertyDelegate: PropertyDelegate): GitHubRepositoryCredentials? {
        try {
            return GitHubRepositoryCredentials(
                propertyDelegate.resolveTo<String>(usernameProperty) ?: "",
                requireNotNullOrEmpty(propertyDelegate.resolveTo(tokenProperty))
            )
        } catch (ex: IllegalArgumentException) {
            val allowEmptyCredentials = propertyDelegate.resolveTo<String?>("com.moneyforward.allow-empty-credentials")

            if (allowEmptyCredentials?.toBoolean() ?: PrivateRepositoryPlugin.PROJECT_PLUGIN_DATA.allowEmptyCredentials) {
                return null
            }

            throw NullPointerException("Could not find github credentials. If building locally please configure " +
                    "and run the `storeGitHubCredentials` task or check the following properties:" +
                    "\n\t- $usernameProperty\n\t- $tokenProperty")
        }
    }

    private fun requireNotNullOrEmpty(string: String?): String {
        if (string.isNullOrEmpty()) throw IllegalArgumentException("String cannot be null or empty")
        return string
    }
}