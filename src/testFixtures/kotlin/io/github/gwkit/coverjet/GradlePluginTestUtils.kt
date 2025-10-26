package io.github.gwkit.coverjet

import io.github.gwkit.coverjet.gradle.provider.TestKitFileProvider
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.file.shouldBeAFile
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

val GRADLE_HOME: String
    get() {
        val userHome: String = System.getProperty("user.home") ?: error("Cannot obtain 'user.home'.")
        return Path(userHome, ".gradle").absolutePathString()
    }

fun buildGradleRunner(
    projectRoot: File
): GradleRunner {
    return GradleRunner.create()
        .withPluginClasspath()
        .withProjectDir(projectRoot)
        .withTestKitDir(
            projectRoot.resolve(GRADLE_HOME).apply { mkdirs() }
        )
        .apply {
            // gradle testkit support
            val testKitPath: String = System.getProperty(TestKitFileProvider.TEST_KIT_FILE)
            File(projectDir, "gradle.properties").appendText(
                File(testKitPath).readText()
            )
        }
}

fun GradleRunner.runTask(vararg task: String): BuildResult {
    return tasksWithDebugOption(*task).build()
}

fun GradleRunner.runTaskAndFail(vararg task: String): BuildResult {
    return tasksWithDebugOption(*task).buildAndFail()
}

private fun GradleRunner.tasksWithDebugOption(vararg task: String): GradleRunner {
    val arguments: List<String> = mutableListOf(*task) + "-si"
    return withArguments(*arguments.toTypedArray())
}

fun BuildResult.assertOutputContainsStrings(vararg expectedString: String): BuildResult {
    assertSoftly(output) {
        expectedString.forEach {
            shouldContain(it)
        }
    }
    return this
}

fun BuildResult.printLogs(enabled: Boolean): BuildResult {
    if (enabled) {
        println(
            """
            =================== <Build logs> ===================
            $output
            =================== </Build logs> ==================
        """.trimIndent()
        )
    }
    return this
}

/**
 * Asserts that all CoverJet plugin output files exist for the given task.
 *
 * @param buildDir The build directory (typically `rootProjectDir.resolve("build")` for root project,
 *                 or `rootProjectDir.resolve("module1/build")` for subprojects)
 * @param taskName The name of the test task (e.g., "test", "intTest")
 */
fun assertCoverJetTaskOutputFiles(buildDir: File, taskName: String) {
    assertSoftly(buildDir) {
        resolve("coverage/${taskName}.ic").shouldBeAFile()
        resolve("tmp/${taskName}TestKitProperties/testkit-gradle.properties").shouldBeAFile()
        resolve("tmp/${taskName}CovAgentArgs/intellij-agent.args").shouldBeAFile()
        resolve("tmp/${taskName}CovJvmParameter/jvm-agent.arg").shouldBeAFile()
    }
}
