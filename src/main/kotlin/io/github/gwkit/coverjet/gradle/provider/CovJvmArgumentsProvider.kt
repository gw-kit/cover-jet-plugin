package io.github.gwkit.coverjet.gradle.provider

import io.github.gwkit.coverjet.gradle.CoverJetExtension
import io.github.gwkit.coverjet.gradle.task.CovAgentProperties
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.process.CommandLineArgumentProvider
import java.io.File

internal class CovJvmArgumentsProvider(
    private val extension: CoverJetExtension,
    private val covAgentProvider: Provider<File>,
    private val agentPropertiesProvider: TaskProvider<CovAgentProperties>
) : CommandLineArgumentProvider {

    override fun asArguments(): MutableIterable<String> {
        return covAgentProvider.agentFilePath
            .zip(agentPropertiesProvider.propsFilePath) { agentJar, propFile ->
                listOf(
                    "-javaagent:${agentJar}=${propFile}",
                    enableTracingProperty.jvmProp,
                    PRINT_ONLY_ERRORS.jvmProp,
                    IGNORE_STATIC_CONSTRUCTORS.jvmProp,
                    countHitsProperty.jvmProp,
                )
            }
            .get()
            .toMutableList()
    }

    private val enableTracingProperty: String
        get() = "$ENABLE_TRACING_PROP_NAME=${extension.enableTracing.get()}"

    private val countHitsProperty: String
        get() = "$DO_NOT_COUNT_HIT_AMOUNT_PROP_NAME=${extension.countHits.get()}"

    private val Provider<File>.agentFilePath: Provider<String>
        get() = map { it.absolutePath }

    private val TaskProvider<CovAgentProperties>.propsFilePath: Provider<String>
        get() = flatMap { it.propertiesFile.asFile }.map { it.absolutePath }

    private val String.jvmProp: String
        get() = "-D$this"

    companion object {
        private const val ENABLE_TRACING_PROP_NAME = "idea.new.tracing.coverage"
        private const val DO_NOT_COUNT_HIT_AMOUNT_PROP_NAME = "idea.coverage.calculate.hits"

        private const val PRINT_ONLY_ERRORS = "idea.coverage.log.level=error"
        private const val IGNORE_STATIC_CONSTRUCTORS = "coverage.ignore.private.constructor.util.class=true"
    }
}
