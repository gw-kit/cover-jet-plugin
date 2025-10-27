---
description: Prepare release - update version, changelog, README, commit and push
---

# Prepare Release

You are tasked with preparing a release by updating version, reviewing changelog, checking README, and committing the changes.

## Steps to complete:

### 0. Determine version and update gradle.properties

1. **Read current version from gradle.properties**
   - The version property is in the format `version=x.y.z`
   - This should match the current release branch `release/x.y.z`

2. **Verify we're on the correct release branch**
   - Check current branch with: `git branch --show-current`
   - Should be on `release/<version>` branch
   - If not on a release branch, ask the user which version to release

3. **Update gradle.properties if needed**
   - Ensure the version in gradle.properties matches the release branch
   - Remove any SNAPSHOT suffix if present

### 1. Review and complete CHANGELOG.md

1. **Get the diff between current branch and main**
   - Run: `git log main..HEAD --oneline` to see commits not in main
   - Run: `git diff main...HEAD --stat` to see file changes

2. **Review CHANGELOG.md**
   - Check if there's an "Unreleased" section
   - If yes, convert it to the version number: `## x.y.z`
   - If no, check the git diff and create a new version section with changes
   - Ensure all significant changes from the diff are documented
   - Follow the existing changelog format (bullet points, grouped by type if needed)

3. **Verify changelog completeness**
   - Read recent commits and ensure all user-facing changes are documented
   - Check for:
     - New features
     - Bug fixes
     - Breaking changes
     - Dependency updates (Gradle, etc.)
   - Ask the user if the changelog looks complete

### 2. Check and update README.md

1. **Review README.md compatibility table**
   - Locate the compatibility table (around line 17)
   - The table should include: CoverJet plugin version, Gradle version, min JVM, and min Kotlin version
   - Example format:
     ```
     | CoverJet plugin | Gradle                 | min JVM | min Kotlin |
     |-----------------|------------------------|---------|------------|
     | **0.1.+**       | **8.14+** - **9.1+**   | 17      | 1.9.0      |
     ```

2. **Update compatibility table**
   - Verify Gradle version ranges are up to date
   - Check minimum JVM version is correct
   - **Set minimum Kotlin version**:
     - Check `gradle/deps.versions.toml` for current Kotlin version
     - Determine minimum compatible Kotlin version for this release
     - Update the table with the min Kotlin version for the current plugin version row
   - If adding a new row for the current release, copy the previous row and update versions

3. **Review other README sections**
   - Check for any outdated information
   - Ensure installation instructions reference correct versions
   - Verify any new features are documented
   - Ask the user if any README updates are needed

### 3. Run tests to verify release

1. **Run full test suite**
   - Execute: `./gradlew build functionalTest detektMain detektTest --continue`
   - Ensure all tests pass
   - If tests fail, stop the release process and report the failures

### 4. Commit and push

1. **Stage the changes**
   - Add modified files: `git add gradle.properties CHANGELOG.md README.md`
   - Also add any other files that were updated during the release preparation

2. **Create release commit**
   - Commit with message: `Release version <x.y.z>`
   - Do NOT add the Claude Code attribution for release commits
   - Example: `git commit -m "Release version 0.1.4"`

3. **Provide next steps**
   - Inform the user that the release branch is ready
   - Remind them to:
     - Create a PR from `release/<version>` to `main`
     - After merge, tag the release: `git tag v<version>`
     - Push the tag: `git push origin v<version>`
     - Publish to plugin portal if needed

## Important Notes:

- **Do NOT push to main directly** - always use release branches
- **Verify tests pass** before committing
- **Check that version is consistent** across gradle.properties, changelog, and branch name
- **Review all changes carefully** - this is a release!
- The version format is semantic versioning: `MAJOR.MINOR.PATCH`
- Release branches follow the pattern: `release/<version>`

## Error Handling:

- If not on a release branch, ask the user to checkout or create one
- If tests fail, report failures and stop the process
- If changelog is incomplete, ask the user for missing information
- If version mismatch detected, ask the user to clarify the correct version
