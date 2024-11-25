package com.moneyforward.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

abstract class StoreGitHubCredentialsTask : DefaultTask() {
    /**
     * The output directory of the gradle.properties file. Defaults to the global HOME directory
     */
    @get:InputDirectory @get:Optional
    abstract val outputDirectory: DirectoryProperty

    private val credentialEntries = mutableListOf<StoreGitHubCredentialsEntry>()

    /**
     * Adds the default credentials entry, this does not need to be invoked unless
     * a manual entry was already added via [addEntry]
     * @see [addEntry]
     */
    fun withDefaultEntry(entry: StoreGitHubCredentialsEntry.() -> Unit = {}) {
        credentialEntries.add(StoreGitHubCredentialsEntry().apply(entry))
    }

    /**
     * Adds a credentials entry to be managed in the local gradle.properties file
     * @param propertyPrefix (optional) property prefix to be used for the `username` and `token` property
     * @see [withDefaultEntry]
     */
    fun addEntry(propertyPrefix: String? = null, entry: StoreGitHubCredentialsEntry.() -> Unit = {}) {
        val credentialsEntry = StoreGitHubCredentialsEntry()
        credentialEntries.add(credentialsEntry.copy(
            usernameProperty = propertyPrefix?.let { "$propertyPrefix.username" } ?: credentialsEntry.usernameProperty,
            tokenProperty = propertyPrefix?.let { "$propertyPrefix.token" } ?: credentialsEntry.tokenProperty
        ).apply(entry))
    }

    /**
     * Creates or modifies the local gradle.properties file to include GitHub credentials as properties
     * to be used for dependency resolution
     */
    @TaskAction
    internal fun storeGitHubCredentials() {
        val outputDirectory = outputDirectory.orNull?.asFile?.absolutePath?.let { Path.of(it) } ?: Paths.get(
            System.getenv("HOME") ?: "", "/.gradle/"
        )

        val credentialsFile = outputDirectory.resolve("gradle.properties").toFile()
        // add the default store GitHub credentials entry if no other entries are defined
        if (credentialEntries.isEmpty()) credentialEntries.add(StoreGitHubCredentialsEntry())

        val newEntries = credentialEntries.mapNotNull { processCredentialsFileChanges(credentialsFile, it) }
        if (newEntries.isNotEmpty()) {
            credentialsFile.appendText('\n' + newEntries.joinToString(separator = "\n"))
            logger.info("Appended ${newEntries.size} new entries to gradle.properties file")
        } else {
            logger.info("No changes made to gradle.properties")
        }
    }

    private fun processCredentialsFileChanges(credentialsFile: File, entry: StoreGitHubCredentialsEntry): String? {
        val checkFlags = checkCredentialsFile(credentialsFile, entry)
        if (checkFlags == NONE) return null
        logger.debug("Entry ({}, {}), flag value = {}", entry.usernameProperty, entry.tokenProperty, checkFlags)

        val username: String? = entry.username ?: System.getenv("GRADLE_GITHUB_USERNAME")
        val token: String? = entry.token ?: System.getenv("GRADLE_GITHUB_TOKEN")

        val newLines = mutableListOf<String>()

        if ((checkFlags and USERNAME_FLAG) > 0 && username != null) {
            newLines.add("${entry.usernameProperty}=$username")
        }
        if ((checkFlags and TOKEN_FLAG) > 0) {
            if (token.isNullOrEmpty()) {
                throw NullPointerException("GitHub token cannot be null or empty. " +
                        "Please check your environment variables or configure the task to use the correct username!")
            }
            newLines.add("${entry.tokenProperty}=$token")
        }

        return newLines.joinToString(separator = "\n")
    }

    private fun checkCredentialsFile(file: File, entry: StoreGitHubCredentialsEntry): Int {
        if (!file.exists()) return ALL_FLAGS
        val contents = file.readText()
        var properties = NONE
        if (!contents.contains(entry.usernameProperty)) {
            properties = properties or USERNAME_FLAG
        }
        if (!contents.contains(entry.tokenProperty)) {
            properties = properties or TOKEN_FLAG
        }
        return properties
    }

    companion object {
        private const val NONE          = 0
        private const val USERNAME_FLAG = 1
        private const val TOKEN_FLAG    = 2
        private const val ALL_FLAGS     = 3
        internal const val NAME = "storeGitHubCredentials"
    }
}