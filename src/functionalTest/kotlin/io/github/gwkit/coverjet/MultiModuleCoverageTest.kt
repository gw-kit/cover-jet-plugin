package io.github.gwkit.coverjet

import io.github.gwkit.coverjet.test.GradlePluginTest
import io.github.gwkit.coverjet.test.GradleRunnerInstance
import io.github.gwkit.coverjet.test.RootProjectDir
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.file.shouldBeAFile
import io.kotest.matchers.file.shouldExist
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

@GradlePluginTest(TestProjects.MULTI_MODULE, kts = true)
class MultiModuleCoverageTest {

    @RootProjectDir
    lateinit var rootProjectDir: File

    @GradleRunnerInstance
    lateinit var gradleRunner: GradleRunner

    @ParameterizedTest
    @ValueSource(
        strings = [
            "8.14.3",
        ]
    )
    fun `all modules should include patterns for all other modules`(
        gradleVersion: String,
    ) {
        // WHEN: Run tests for module1
        gradleRunner
            .withGradleVersion(gradleVersion)
            .runTask(":module1:testCovAgentArgs", ":module2:testCovAgentArgs")
            .printLogs(false)

        // THEN: Check module1's agent args include both module1 and module2 patterns
        // THEN: Check module2's agent args also include both patterns
        assertIntellijAgentArgs(
            "module1/build/tmp/testCovAgentArgs/intellij-agent.args",
            "module2/build/tmp/testCovAgentArgs/intellij-agent.args",
        )
    }

    private fun assertIntellijAgentArgs(vararg intellijAgentArgsFile: String) {
        sequenceOf(*intellijAgentArgsFile).forEach {
            assertSoftly(rootProjectDir.resolve(it)) {
                shouldExist()
                shouldBeAFile()

                readLines()
                    .shouldContain("com\\.module1\\..*")
                    .shouldContain("com\\.module2\\..*")
            }
        }
    }
}
