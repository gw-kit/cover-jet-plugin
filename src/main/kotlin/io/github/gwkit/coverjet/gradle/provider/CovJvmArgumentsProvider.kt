package io.github.gwkit.coverjet.gradle.provider

import io.github.gwkit.coverjet.gradle.task.CovAgentProperties
import io.github.gwkit.coverjet.gradle.task.CovAgentTask
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.process.CommandLineArgumentProvider
import javax.inject.Inject

internal class CovJvmArgumentsProvider @Inject constructor(
    private val covAgentProvider: TaskProvider<CovAgentTask>,
    private val agentPropertiesProvider: TaskProvider<CovAgentProperties>
) : CommandLineArgumentProvider {

    override fun asArguments(): MutableIterable<String> {
        return covAgentProvider.agentFilePath
            .zip(agentPropertiesProvider.propsFilePath) { agentJar, propFile ->
                listOf(
                    "-javaagent:${agentJar}=${propFile}",
                    ENABLE_TRACING.jvmProp,
                    PRINT_ONLY_ERRORS.jvmProp,
                    IGNORE_STATIC_CONSTRUCTORS.jvmProp,
                    DO_NOT_COUNT_HIT_AMOUNT.jvmProp,
                )
            }
            .get()
            .toMutableList()
    }

    private val TaskProvider<CovAgentTask>.agentFilePath: Provider<String>
        get() = flatMap { it.agentFile }.map { it.absolutePath }

    private val TaskProvider<CovAgentProperties>.propsFilePath: Provider<String>
        get() = flatMap { it.propertiesFile.asFile }.map { it.absolutePath }

    private val String.jvmProp: String
        get() = "-D$this"

    companion object {
        private const val ENABLE_TRACING = "idea.new.tracing.coverage=true"
        private const val PRINT_ONLY_ERRORS = "idea.coverage.log.level=error"
        private const val IGNORE_STATIC_CONSTRUCTORS = "coverage.ignore.private.constructor.util.class=true"
        private const val DO_NOT_COUNT_HIT_AMOUNT = "idea.coverage.calculate.hits=false"
    }
}
