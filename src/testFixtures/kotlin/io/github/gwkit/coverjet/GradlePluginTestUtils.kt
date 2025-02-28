package io.github.gwkit.coverjet

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.string.shouldContain
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString

private const val TEST_KIT_PROPS_FILE = "functionalTest-testkit-gradle.properties"

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
            javaClass.classLoader.getResourceAsStream(TEST_KIT_PROPS_FILE)?.use { inputStream ->
                File(projectDir, "gradle.properties").outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
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
