package com.moneyforward.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths

abstract class StoreGitHubCredentialsTask : DefaultTask() {
    @get:InputDirectory @get:Optional
    abstract val outputDirectory: DirectoryProperty

    @get:Input @get:Optional
    abstract val prompts: Property<String>
    @get:Input @get:Optional
    abstract val allowRubyFallbackEnvVariable: Property<Boolean>

    private val credentialEntries = mutableListOf<StoreGitHubCredentialsEntry>()
    private val allowPrompting get() = prompts.getOrElse("true").toBoolean()

    fun withDefaultEntry() {
        credentialEntries.add(StoreGitHubCredentialsEntry())
    }

    fun addEntry(propertyPrefix: String? = null, entry: StoreGitHubCredentialsEntry.() -> Unit = {}) {
        val credentialsEntry = StoreGitHubCredentialsEntry()
        credentialEntries.add(credentialsEntry.copy(
            usernameProperty = propertyPrefix?.let { "$propertyPrefix.username" } ?: credentialsEntry.usernameProperty,
            tokenProperty = propertyPrefix?.let { "$propertyPrefix.token" } ?: credentialsEntry.tokenProperty
        ).apply(entry))
    }

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

        var username: String? = entry.username ?: System.getenv("GRADLE_GITHUB_USERNAME")
        var token: String? = entry.token ?: System.getenv("GRADLE_GITHUB_TOKEN")

        val invalidUsername = username.isNullOrEmpty()
        val invalidToken = token.isNullOrEmpty()

        if (allowRubyFallbackEnvVariable.getOrElse(false) && (invalidUsername || invalidToken)) {
            logger.debug("Using Ruby Gems fallback environment variable")
            val rubyBundleEnvValue = System.getenv("BUNDLE_RUBYGEMS__PKG__GITHUB__COM")
            val rubySplitValue = rubyBundleEnvValue?.split(':')
            if (invalidUsername) username = rubySplitValue?.get(0)
            if (invalidToken) token = rubySplitValue?.get(1)
        }

        val userInput = UserInput.create()

        val newLines = mutableListOf<String>()

        if ((checkFlags and USERNAME_FLAG) > 0) {
            if (username.isNullOrEmpty()) {
                if (!allowPrompting) throw NullPointerException("GitHub username cannot be null or empty. " +
                        "Please check your environment variables or configure the task to use the correct username!")
                else username = userInput.prompt("Enter `${entry.usernameProperty}` username: ")
            }
            newLines.add("${entry.usernameProperty}=$username")
        }
        if ((checkFlags and TOKEN_FLAG) > 0) {
            if (token.isNullOrEmpty()) {
                if (!allowPrompting) throw NullPointerException("GitHub token cannot be null or empty. " +
                        "Please check your environment variables or configure the task to use the correct username!")
                else token = userInput.prompt("Enter `${entry.tokenProperty}` token: ")
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

    private companion object {
        const val NONE          = 0
        const val USERNAME_FLAG = 1
        const val TOKEN_FLAG    = 2
        const val ALL_FLAGS     = 3
    }
}