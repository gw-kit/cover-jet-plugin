# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CoverJet is a lightweight Gradle plugin that uses IntelliJ Coverage engine to collect coverage data from tests. It provides infrastructure to collect coverage from both regular tests and GradleRunner tests (for plugin development).

**Key characteristics:**
- Gradle plugin written in Kotlin
- Supports Gradle 8.14+ with JVM 17+
- Uses IntelliJ Coverage agent for collecting coverage data
- Coverage data saved in binary format at `build/coverage/<test-task-name>.ic`

## Build Commands

### Basic build and test
```bash
./gradlew build
```
This runs all tests (unit + functional) and assembles the plugin.

### Run unit tests only
```bash
./gradlew test
```

### Run functional tests only
```bash
./gradlew functionalTest
```

### Run single test
```bash
./gradlew test --tests "ClassName.testMethodName"
./gradlew functionalTest --tests "ClassName.testMethodName"
```

### Disable parallel tests
```bash
./gradlew test -PdisableParallelTests
```

### Run linting
```bash
./gradlew detektMain detektTest
```

### Full build with checks
```bash
./gradlew build detektMain detektTest --continue
```

### Delta coverage check
```bash
./gradlew deltaCoverage -PdiffBase="refs/remotes/origin/main"
```

### Publish snapshot to GitHub Packages
```bash
./gradlew publishAllPublicationsToGhPackagesRepository -PsnapshotPrefix='SNAPSHOT.<id>'
```

## Architecture

### Plugin Entry Point
The main plugin class is `CoverJetPlugin` (src/main/kotlin/io/github/gwkit/coverjet/gradle/CoverJetPlugin.kt). It:
1. Creates `coverJet` extension for configuration
2. Registers IntelliJ Coverage agent JAR dependency
3. Hooks into all `Test` tasks to inject coverage agent JVM args
4. Generates TestKit properties for GradleRunner support

### Coverage Collection Flow
1. Plugin registers agent configuration and resolves IntelliJ Coverage agent JAR
2. For each `Test` task, generates:
   - `CovAgentProperties` task - creates coverage agent arguments
   - `CovJvmParameter` task - combines agent JAR + arguments
   - `GenTestKitProperties` task - creates gradle.properties for TestKit
3. Agent JVM args injected via `CovJvmArgumentsProvider`
4. TestKit support injected via `TestKitFileProvider` and resource processing

### Key Components
- **CoverJetExtension**: DSL configuration (intellijCoverageVersion)
- **CoverageAgentGradleConfiguration**: Manages agent dependency resolution
- **CovJvmArgumentsProvider**: Provides -javaagent args to test JVM
- **TestKitFileProvider**: Provides TestKit properties as system property
- **GenTestKitProperties**: Generates gradle.properties for GradleRunner

### Build Conventions
The project uses precompiled script plugins in `buildSrc/` for shared configuration:
- **jvm-project-conventions**: Kotlin JVM toolchain (Java 17), detekt, publishing
- **unit-tests-conventions**: JUnit 5 setup with parallel execution, test dependencies
- **functional-tests-conventions**: Separate functional test suite using GradleRunner
- **delta-coverage-conventions**: Delta coverage integration using IntelliJ engine
- **gh-publish-conventions**: GitHub Packages publishing configuration

### Dependency Management
Dependencies defined in `gradle/deps.versions.toml` version catalog, accessed in buildSrc via `libDeps` extension (see `ProjectExtensions.kt`).

### Test Structure
- **Unit tests**: Standard tests in `src/test/` (none currently in this structure)
- **Functional tests**: In `src/functionalTest/`, use GradleRunner to test plugin behavior
- **Test fixtures**: Shared test utilities in `src/testFixtures/`

Functional tests run with:
- Max 4 parallel forks
- JUnit lifecycle per class
- Disabled up-to-date checks to ensure fresh runs

### Coverage Reporting
Uses delta-coverage-gradle plugin with IntelliJ Coverage engine:
- Coverage diffs against origin/main (configurable via `-PdiffBase`)
- Generates HTML, XML, and Markdown reports
- Full coverage reports enabled
- CI runs delta coverage on PRs only

## Development Workflow

### Branch Strategy
- Main branch: `main` (not set as default in local repo)
- Development branch: `develop`
- Release branches: `release/**`

### CI/CD
GitHub Actions build workflow (.github/workflows/build.yaml):
- Runs on push to develop/main/release branches and all PRs
- Uses Gradle build cache and configuration cache
- Runs: `build detektMain detektTest --continue`
- Publishes snapshots on develop push or with `publish-snapshot-artifacts` label
- Runs delta coverage on PRs (can suppress with `suppress-delta-coverage` label)
- Publishes test results and coverage reports to PR

### Testing GradleRunner Support
When testing the plugin's GradleRunner integration:
```kotlin
val gradleRunner = GradleRunner.create()
    .withPluginClasspath()
    .withProjectDir(projectRoot)
    .apply {
        val testKitPath: String = System.getProperty("io.github.gwkit.coverjet.test-kit")
        File(projectDir, "gradle.properties").appendText(
            File(testKitPath).readText()
        )
    }
```

## Configuration Extension

The plugin exposes `coverJet` extension:
```kotlin
coverJet {
    intellijCoverageVersion.set("1.0.744") // Default
}
```