package com.moneyforward.gradle

import org.gradle.api.Project

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

    override fun getCredentials(project: Project): GitHubRepositoryCredentials? {
        try {
            return GitHubRepositoryCredentials(
                requireNotNullOrEmpty(project.findProperty(usernameProperty) as String?),
                requireNotNullOrEmpty(project.findProperty(tokenProperty) as String?)
            )
        } catch (ex: IllegalArgumentException) {
            val allowEmptyCredentials = project.findProperty("com.moneyforward.allow-empty-credentials") as String?

            if (allowEmptyCredentials?.toBoolean() ?: PrivateRepositoryPlugin.PLUGIN_DATA.allowEmptyCredentials) {
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