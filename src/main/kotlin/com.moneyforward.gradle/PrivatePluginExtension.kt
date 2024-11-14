package com.moneyforward.gradle

import org.gradle.api.initialization.Settings


fun Settings.privatePlugins(spec: PrivateRepositoryConfiguration.() -> Unit) {
    this.pluginManagement.repositories.private(spec)
}
