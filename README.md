## Private-Repository-Plugin
The `private-repository-plugin` is a Gradle plugin for making dependencies on private Maven repositories easier. It supports **GitHub Packages** and **AWS CodeArtifact** out of the box.

## Installation
```kotlin
plugins {
    id("com.moneyforward.private-repository-plugin") version "0.4.0"
}
```

## Usage
The plugin introduces two main features: dependency resolution and credential management.

### Dependency Resolution

Use the `private` extension on `RepositoryHandler` to declare private repositories:

```kotlin
// build.gradle.kts
repositories {
    private {
        allowEmptyCredentials = false // optional, defaults to false

        // GitHub Packages — full URL
        repository("https://maven.pkg.github.com/OWNER/REPOSITORY")

        // GitHub Packages — shorthand helper
        repository(gpr("OWNER", "REPOSITORY"))

        // GitHub Packages — with explicit credentials
        repository(gpr("OWNER", "REPOSITORY")) {
            credentialProvider = StaticCredentialProvider(
                username = System.getenv("GITHUB_USERNAME"),
                token = System.getenv("GITHUB_TOKEN")
            )
        }

        // AWS CodeArtifact
        codeArtifactRepository(
            domain = "my-domain",
            repository = "my-repo",
            domainOwner = 123456789012L,   // optional: AWS account ID
            ssoProfile = "my-sso-profile", // optional: named AWS SSO profile
        )

        // AWS CodeArtifact — builder-block style
        codeArtifactRepository {
            domain = "my-domain"
            repository = "my-repo"
        }
    }
}
```

The plugin can also be applied in `settings.gradle.kts` to add resolution for plugins used inside build files:

```kotlin
// settings.gradle.kts
plugins {
    id("com.moneyforward.private-repository-plugin") version "0.4.0"
}

privatePlugins {
    repository(gpr("OWNER", "REPO"))

    codeArtifactRepository(
        domain = "my-domain",
        repository = "my-repo",
    )
}
```

#### GitHub Packages default credentials

By default, GitHub Packages repositories read credentials from Gradle project properties:

```properties
# ~/.gradle/gradle.properties
private-repository.github.username=YOUR_GITHUB_USERNAME
private-repository.github.token=YOUR_GITHUB_TOKEN
```

#### AWS CodeArtifact credentials

CodeArtifact repositories fetch a short-lived authorization token from AWS at build time using the
[AWS SDK default credential chain](https://docs.aws.amazon.com/sdk-for-kotlin/latest/developer-guide/credential-providers.html).
Specify `ssoProfile` to authenticate via a named AWS SSO profile instead.

**IMPORTANT:** If the plugin cannot find valid credentials it will throw an exception, even if no
dependency resolution is in progress. This can be disabled via the `com.moneyforward.allow-empty-credentials`
Gradle property or the `allowEmptyCredentials` field inside the `private` block.

---

### Credential Management

The plugin registers a **manual** Gradle task `storeRepositoryCredentials` that appends GitHub
credentials to the system `gradle.properties` file.

By default, credentials are read from the `GRADLE_PRIVATE_REPO_USERNAME` and `GRADLE_PRIVATE_REPO_TOKEN`
environment variables and written as `private-repository.github.username` and
`private-repository.github.token`.

The task can be extended to handle multiple credential sets:

```kotlin
// build.gradle.kts
tasks.named("storeRepositoryCredentials", StoreRepositoryCredentialsTask::class) {
    withDefaultEntry() // include the default private-repository.github.* entry

    addEntry("private.company2.github") { // writes private.company2.github.username / .token
        username = System.getenv("COMPANY2_GITHUB_USERNAME")
        token = System.getenv("COMPANY2_GITHUB_TOKEN")
    }
}
```

---

### Motivation

Multiple private repository dependencies normally require duplicating a `maven { ... }` block for
each one, leading to lengthy build files. This plugin collapses all of that into a single `private`
block with a concise DSL.

The `storeRepositoryCredentials` task solves the CI/CD-to-Docker-image credential handoff problem
by automatically translating environment variables into `gradle.properties` entries.