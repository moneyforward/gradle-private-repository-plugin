plugins {
    kotlin("jvm") version "2.0.20"
    id("com.gradle.plugin-publish") version "1.2.1"
    `java-gradle-plugin`
}

group = "com.moneyforward.gradle"
version = "0.0.0"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    implementation("org.jline:jline-reader:3.27.1")
}

gradlePlugin {
    plugins {
        create("privateRepositoryPlugin") {
            displayName = "Private Repository Plugin"
            description = "This plugin eases the "
            id = "com.moneyforward.private-repository-plugin"
            implementationClass = "com.moneyforward.gradle.PrivateRepositoryPlugin"
        }
    }
}