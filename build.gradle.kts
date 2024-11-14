plugins {
    kotlin("jvm") version "2.0.20"
    id("com.gradle.plugin-publish") version "1.2.1"
    `java-gradle-plugin`
}

group = "io.github.evancmfw.gradle"
version = "0.1.1"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

gradlePlugin {
    website = "https://github.com/moneyforward/gradle-private-repository-plugin"
    vcsUrl = "https://github.com/moneyforward/gradle-private-repository-plugin"
    plugins {
        create("privateRepositoryPlugin") {
            displayName = "Private Repository Plugin"
            description = "This plugin reduces repetition when specifying private GitHub packages as gradle dependencies"
            id = "io.github.evanc-mfw.private-repository-plugin"
            implementationClass = "io.github.evancmfw.gradle.PrivateRepositoryPlugin"
            tags = listOf("tooling", "kotlin")
        }
    }
}