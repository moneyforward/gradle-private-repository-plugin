## Private-Repository-Plugin
The `private-repository-plugin` is a simple plugin for making dependencies on private GitHub packages easier.

## Installation
Simply include the following as a gradle plugin:
```yaml
plugins {
  id("com.moneyforward.private-repository") version "0.3.2"
}
```

## Usage
The `private-repository-plugin` consists of two main parts: dependency resolution and credential management.
### Dependency Resolution
This plugin introduces the `.private` extension to the gradle `RepositoryHandler`. A complete setup is as follows:
```kotlin
repositories {
    private { // plugin extension method
        allowEmptyCredentials = false // optional, defaults to false
        // reference to your GitHub package dependency
        repository("https://maven.pkg.github.com/OWNER/REPOSITORY")
        repository(gpr("OWNER", "REPOSITORY")) // use gpr function for shortcut to GitHub packages URL
        repository("https://maven.pkg.github.com/OWNER/OTHER_REPOSITORY") {
            // example for providing specific username and token to use in resolution
            credentialsProvider = StaticCredentialsProvider(
                username = System.getEnv("GITHUB_USERNAME"),
                token = System.getEnv("GITHUB_TOKEN")
            )
        }
    }
}
```

The plugin can also be used in the `settings.gradle` file to add resolution for plugins within build files.
```kotlin
// settings.gradle.kts
plugins {
    id("com.moneyforward.private-repository-plugin") version LATEST_VERSION_HERE
}
// additional block to specify repositories, with same configuration as used in build file
privatePlugins {
    repository(gpr("OWNER", "REPO"))
}
```
By default, repositories will use the following properties from the project's properties:
```properties
private-repository.github.username=YOUR_GITHUB_USERNAME
private-repository.github.token=YOUR_GITHUB_TOKEN
```
**IMPORTANT** If the plugin CANNOT find valid credentials it will throw an exception, even if a build is not in progress.
This can be disabled via the `com.moneyforward.allow-empty-credentials` property OR with the `allowEmptyCredentials`
field within the `private` block.

### Credential Management
The `private-repository-plugin` introduces a **manual** gradle task `storeGitHubCredentials` which automatically appends
your GitHub credentials to the system `gradle.properties`.

By default, these credentials are picked up from the 
`GRADLE_GITHUB_USERNAME` and `GRADLE_GITHUB_TOKEN` environment variables and are appended as `private-repository.github.username`
and `private-repository.github.token` respectively.

However, the task also provides a configuration for introducing other
properties and ability to specify the credentials.
```kotlin
// kotlin-dsl
tasks.named("storeGitHubCredentials", StoreGitHubCredentialsTask::class) {
    withDefaultEntry() // specify usage of private-repository.github... required when adding other entries
    addEntry("private.company2.github") { // add entry for private.company2.github.username and private.company2.github.token
        // specify username and token via non-default environment variables
        username = System.getenv("COMPANY2_GITHUB_USERNAME")
        token = System.getenv("COMPANY2_GITHUB_TOKEN")
    }
}
```

### Motivation
Multiple dependencies on private GitHub packages require the duplication of the same block of code, as each repository
requires its own maven block. This can result in lengthy build.gradle files.

Additionally, translating environment variables (used in CI/CD) to gradle.properties (used in docker images) is an
annoying process to stream-line. This plugin addresses the problem via the custom gradle task that can be ran in CI/CD
which automatically applies environment variables to the gradle.properties.
