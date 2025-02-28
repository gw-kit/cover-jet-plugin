package io.github.gwkit.coverjet.gradle.task

import io.github.gwkit.coverjet.gradle.util.getSourceSet
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import java.io.File
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries

internal fun Project.registerGenCoverageAgentProperties(
    taskName: String,
): TaskProvider<CovAgentProperties> {
    return tasks.register("${taskName}CovAgentProperties", CovAgentProperties::class.java) { task ->
        task.taskName.set(taskName)
    }
}

open class CovAgentProperties @Inject constructor(
    objects: ObjectFactory,
) : DefaultTask() {

    @Input
    val taskName: Property<String> = objects.property(String::class.java)

    @Input
    val binaryCoverageFilePath: Property<String> = objects.property(String::class.java).convention(
        taskName.zip(project.layout.buildDirectory) { task, buildDir ->
            buildDir.file("coverage/$task.ic").asFile.absolutePath
        }
    )

    @OutputFile
    val propertiesFile: RegularFileProperty = objects.fileProperty().convention {
        temporaryDir.resolve("intellij-agent.args")
    }

    @TaskAction
    fun genProperties() {
        val includePatterns: Set<String> = project.buildIncludeSourcesPatterns().get()

        propertiesFile.get().asFile.printWriter().use { pw ->
            with(pw) {
                appendLine(binaryCoverageFilePath.get())
                appendLine(TRACKING_PER_TEST.toString())
                appendLine(CALCULATE_FOR_UNLOADED_CLASSES.toString())
                appendLine(APPEND_TO_DATA_FILE.toString())
                appendLine(LINING_ONLY_MODE.toString())

                includePatterns.forEach { appendLine(it) }

                appendLine("-exclude")
                appendLine("android\\..*")
                appendLine("com\\.android\\..*")
                appendLine("org\\.jetbrains\\.kotlin\\.gradle\\..*")
            }
        }
    }

    private fun Project.buildIncludeSourcesPatterns(): Provider<Set<String>> {
        return project.rootProject.allprojects.map { proj ->
            proj.getSourceSet("main").map { it.allJava.srcDirs }
        }.fold(
            project.provider<Set<File>> { emptySet() },
            ::merge
        ).map { allSourceFiles ->
            allSourceFiles.asSequence()
                .filter { it.exists() }
                .map { file -> obtainCommonPackage(file) }
                .map { it.toIncludePackageRegex() }
                .toSet()
        }
    }

    private fun <T : Any, C : Iterable<T>> merge(
        first: Provider<out C>,
        second: Provider<out C>
    ): Provider<Iterable<T>> = first.zip(second) { left, right ->
        left + right
    }

    private fun obtainCommonPackage(srcDir: File): String {
        val getSingleChildOrNull: Path.() -> Path? = {
            listDirectoryEntries().singleOrNull()?.takeIf(Path::isDirectory)
        }

        return generateSequence(srcDir.toPath().getSingleChildOrNull()) { currentDir ->
            currentDir.getSingleChildOrNull()
        }
            .map { it.fileName.toString() }
            .joinToString(".")
    }

    private fun String.toIncludePackageRegex(): String {
        return this.replace(".", "\\.") + "\\..*"
    }

    companion object {
        private const val TRACKING_PER_TEST = false
        private const val CALCULATE_FOR_UNLOADED_CLASSES = false
        private const val APPEND_TO_DATA_FILE = true
        private const val LINING_ONLY_MODE = false
    }
}
