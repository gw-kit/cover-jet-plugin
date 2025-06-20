package io.github.gwkit.coverjet

import io.github.gwkit.coverjet.test.GradlePluginTest
import io.github.gwkit.coverjet.test.GradleRunnerInstance
import io.github.gwkit.coverjet.test.ProjectFile
import io.github.gwkit.coverjet.test.RestorableFile
import io.github.gwkit.coverjet.test.RootProjectDir
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.file.shouldBeAFile
import org.gradle.api.plugins.JavaPlugin
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.io.File

@GradlePluginTest(TestProjects.SINGLE_MODULE, kts = true)
class CoverJetFunTest {

    @RootProjectDir
    lateinit var rootProjectDir: File

    @ProjectFile("build.gradle.kts")
    lateinit var buildFile: RestorableFile

    @GradleRunnerInstance
    lateinit var gradleRunner: GradleRunner

    @BeforeEach
    fun beforeEach() {
        buildFile.restoreOriginContent()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "8.13",
            "8.14.2",
//            "9.0.0-rc-1", // TODO: Currently got error, try later
        ]
    )
    fun `test tasks should generate binary coverage files`(
        gradleVersion: String,
    ) {
        // WHEN
        val testTasks = listOf(
            JavaPlugin.TEST_TASK_NAME,
            "intTest",
//            TODO: https://github.com/gradle/gradle/issues/25979
//            "--configuration-cache",
        )

        // THEN
        gradleRunner
            .withGradleVersion(gradleVersion)
            .runTask(*testTasks.toTypedArray())
            .printLogs(false)

        // AND THEN
        val baseReportDirFile = rootProjectDir.resolve("build/")
        assertSoftly(baseReportDirFile) {
            testTasks.forEach { task ->
                resolve("coverage/${task}.ic").shouldBeAFile()
                resolve("tmp/${task}TestKitProperties/testkit-gradle.properties").shouldBeAFile()
                resolve("tmp/${task}CovAgentArgs/intellij-agent.args").shouldBeAFile()
                resolve("tmp/${task}CovJvmParameter/jvm-agent.arg").shouldBeAFile()
            }
        }
    }
}
