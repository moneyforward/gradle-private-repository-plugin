plugins {
    kotlin("jvm") version "2.0.20"
    id("com.gradle.plugin-publish") version "1.2.1"
    `java-gradle-plugin`
}

group = "com.moneyforward.gradle"
version = "0.1.0"

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
            id = "com.moneyforward.private-repository-plugin"
            implementationClass = "com.moneyforward.gradle.PrivateRepositoryPlugin"
        }
    }
}