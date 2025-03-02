# CoverJet Gradle plugin

[![Build](https://github.com/gw-kit/cover-jet-plugin/actions/workflows/build.yaml/badge.svg?branch=main)](https://github.com/gw-kit/cover-jet-plugin/actions/workflows/build.yaml)
[![codecov](https://codecov.io/gh/gw-kit/cover-jet-plugin/graph/badge.svg?token=mVBIf2xll9)](https://codecov.io/gh/gw-kit/cover-jet-plugin)
[![GitHub issues](https://img.shields.io/github/issues/gw-kit/cover-jet-plugin)](https://github.com/gw-kit/cover-jet-plugin/issues)
[![GitHub stars](https://img.shields.io/github/stars/gw-kit/cover-jet-plugin?style=flat-square)](https://github.com/gw-kit/cover-jet-plugin/stargazers)

`CoverJet` is lightweight Gradle plugin that uses [IntelliJ Coverage](https://github.com/JetBrains/intellij-coverage) engine to collect coverage data.
Also, the plugin provides infrastructure to collect coverage from GradleRunner tests(See [Gradle Runner tests](#gradle-runner-tests)).

## Installation

### Compatibility

Delta Coverage plugin compatibility table:

| CoverJet plugin | Gradle                 | min JVM |
|-----------------|------------------------|---------|
| **0.0.+**       | **7.6.4** - **8.13.+** | 17      |    


### Apply plugin

<details open>

<summary><b>Kotlin</b></summary>

```kotlin
plugins {
    id("io.github.gw-kit.cover-jet") version "<the-plugin-version>"
}
```

The latest release version is ![GitHub Release](https://img.shields.io/github/v/release/gw-kit/cover-jet-plugin)

</details>


## Configuration

<details open>
<summary><b>Kotlin</b></summary>

```kotlin
coverJet {
    // Default: 1.0.744
    intellijCoverageVersion.set("<agent-version>")
}

```

</details>

## Execute

```shell
./gradlew test 
```

The test coverage data in binary format will be saved by the path `build/coverage/<test-task-name>.ic`.


## Gradle Runner tests

This section is useful for plugin developers who want to collect coverage data from GradleRunner tests.
Since, GradleRunner tests are executed in a separate JVM, the coverage data is not collected by default.
`CoverJet` plugin generates all required JVM agent properties to collect coverage data.

The only action required from plugin dev is to configure the `GradleRunner` in the next way:

```kotlin
val projectRoot: File = File("<path-to-project>")
val gradleRunner = GradleRunner.create()
    .withPluginClasspath()
    .withProjectDir(projectRoot)
    // other configurations if needed
    .apply {
        // gradle testkit support
        val testKitPath: String = System.getProperty("io.github.gwkit.coverjet.test-kit")
        File(projectDir, "gradle.properties").appendText(
            File(testKitPath).readText()
        )
    }
```

## Generate reports

Coming soon...
