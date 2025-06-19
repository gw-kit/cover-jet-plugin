package io.github.gwkit.coverjet.gradle.task

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
import javax.inject.Inject

internal fun Project.registerCovJvmParameter(
    taskName: String,
    covAgentProvider: Provider<File>,
    agentArgsProvider: TaskProvider<CovAgentProperties>
): TaskProvider<CovJvmParameter> {
    return tasks.register("${taskName}CovJvmParameter", CovJvmParameter::class.java) { task ->
        task.coverageAgentJarPath.set(
            covAgentProvider.map { it.absolutePath }
        )
        task.covAgentPropertiesPath.set(
            agentArgsProvider.flatMap { it.propertiesFile.asFile }.map { it.absolutePath }
        )
    }
}

internal fun TaskProvider<CovJvmParameter>.readJavaAgentParameter(): Provider<List<String>> {
    return flatMap { it.javaAgentParameter.asFile }.map { file -> file.readLines() }
}

internal open class CovJvmParameter @Inject constructor(
    objects: ObjectFactory,
) : DefaultTask() {

    @Input
    val coverageAgentJarPath: Property<String> = objects.property(String::class.java)

    @Input
    val covAgentPropertiesPath: Property<String> = objects.property(String::class.java)

    @OutputFile
    val javaAgentParameter: RegularFileProperty = objects.fileProperty().convention {
        temporaryDir.resolve("jvm-agent.arg")
    }

    @TaskAction
    fun buildJavaAgentProperty() {
        val agentProperty: String = coverageAgentJarPath
            .zip(covAgentPropertiesPath) { agentJar, propFile ->
                listOf(
                    "-javaagent:${agentJar}=${propFile}",
                    ENABLE_TRACING.jvmProp,
                    PRINT_ONLY_ERRORS.jvmProp,
                    IGNORE_STATIC_CONSTRUCTORS.jvmProp,
                    DO_NOT_COUNT_HIT_AMOUNT.jvmProp,
                )
            }
            .get()
            .joinToString("\n")

        javaAgentParameter.asFile.get().writeText(agentProperty)
    }

    private val String.jvmProp: String
        get() = "-D$this"

    companion object {
        private const val ENABLE_TRACING = "idea.new.tracing.coverage=true"
        private const val PRINT_ONLY_ERRORS = "idea.coverage.log.level=error"
        private const val IGNORE_STATIC_CONSTRUCTORS = "coverage.ignore.private.constructor.util.class=true"
        private const val DO_NOT_COUNT_HIT_AMOUNT = "idea.coverage.calculate.hits=false"
    }
}
