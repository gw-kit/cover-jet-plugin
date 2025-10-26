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

1. **Review README.md**
   - Check the compatibility table (around line 17)
   - Verify the version ranges are up to date
   - Check for any outdated information
   - Ensure installation instructions reference correct versions

2. **Update if needed**
   - If there are new Gradle version requirements, update the table
   - If there are new features, ensure they're documented
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

3. **Push to remote**
   - Push the release branch: `git push origin release/<version>`
   - If the branch doesn't exist remotely yet, use: `git push -u origin release/<version>`

4. **Provide next steps**
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