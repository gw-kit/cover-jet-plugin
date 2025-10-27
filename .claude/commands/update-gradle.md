---
description: Update Gradle wrapper to latest version and update functional tests, changelog, and README
---

# Update Gradle

You are tasked with updating the Gradle wrapper to the latest version and updating all related configuration files.

## Steps to complete:

1. **Update Gradle wrapper**
   - Run: `./gradlew wrapper --gradle-version=latest && ./gradlew wrapper`
   - This will download and configure the latest Gradle version

2. **Update functional tests**
   - File: `src/functionalTest/kotlin/io/github/gwkit/coverjet/CoverJetFunTest.kt`
   - Locate the `@ValueSource(strings = [...])` annotation in the test
   - Update the Gradle versions array to include:
     - Latest patch version of Gradle 8.x (e.g., 8.14.2)
     - Latest patch version of Gradle 9.x (e.g., 9.1.0)
     - Any newer major versions if available
   - IMPORTANT: Include the newest version of ALL supported major versions
   - Remove outdated versions but keep at least one older version for backwards compatibility testing

3. **Update CHANGELOG.md**
   - Add a new unreleased section at the top (or current version if preparing release)
   - Document: "Updated Gradle to [version]"
   - Follow the existing changelog format

4. **Update README.md compatibility table**
   - Locate the compatibility table (around line 17)
   - Update the Gradle version ranges if needed
   - The table shows which CoverJet plugin versions support which Gradle versions
   - If the new Gradle version is compatible with current plugin, update the range
   - Example format:
     ```
     | CoverJet plugin | Gradle                 | min JVM |
     |-----------------|------------------------|---------|
     | **0.1.+**       | **8.14.+**             | 17      |
     ```

5. **Verify the updates**
   - Run functional tests to ensure compatibility: `./gradlew functionalTest`
   - Check that all tests pass with the new Gradle versions

## Notes:
- The project currently supports Gradle 8.14+ with JVM 17+
- Functional tests should test both older supported versions and the latest version
- The compatibility table in README should reflect the minimum supported Gradle version