plugins {
    kotlin("jvm") version "2.2.20"
    id("com.gradle.plugin-publish") version "1.2.1"
    id("org.jlleitschuh.gradle.ktlint") version "14.0.1"
    `java-gradle-plugin`
}

group = "com.moneyforward.gradle"
version = "0.6.1"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation("software.amazon.awssdk:codeartifact:2.29.52")
    implementation("software.amazon.awssdk:sso:2.29.52")
    implementation("software.amazon.awssdk:ssooidc:2.29.52")
}

ktlint {
    outputToConsole = true
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
            tags = listOf("tooling", "kotlin")
        }
    }
}
