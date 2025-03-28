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
import org.junit.jupiter.api.Test
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

    @Test
    fun `test tasks should generate binary coverage files`() {
        // WHEN
        val testTasks = listOf(
            JavaPlugin.TEST_TASK_NAME,
            "intTest",
        )

        // THEN
        gradleRunner
            .runTask(*testTasks.toTypedArray())
            .printLogs(false)

        // AND THEN
        val baseReportDirFile = rootProjectDir.resolve("build/")
        assertSoftly(baseReportDirFile) {
            testTasks.forEach { task ->
                resolve("coverage/${task}.ic").shouldBeAFile()
                resolve("testkit/${task}-testkit-gradle.properties").shouldBeAFile()
                resolve("tmp/${task}CovAgentProperties/intellij-agent.args").shouldBeAFile()
            }
        }
    }
}
