package com.moneyforward.gradle

import org.gradle.api.initialization.Settings
import org.gradle.api.provider.Provider

class PrivatePluginRepositoryPlugin : AbstractPrivateRepositoryPlugin<Settings>() {
    companion object {
        val PLUGIN_DATA = PrivateRepositoryConfiguration()
    }
    override val pluginData: PrivateRepositoryConfiguration get() = PLUGIN_DATA

    override fun apply(settings: Settings) {
        settings.dependencyResolutionManagement.repositories.applyRepositoryConfigurations {
            val providers = settings.providers
            providers.gradleProperty(it)
                .orElse { providers.systemProperty(it) }
                .orElse { providers.environmentVariable(it) }
                .orNull
        }
    }

    private fun <T> Provider<T>.orElse(block: () -> Provider<T>): Provider<T> {
        if (isPresent) return this
        return block()
    }
}