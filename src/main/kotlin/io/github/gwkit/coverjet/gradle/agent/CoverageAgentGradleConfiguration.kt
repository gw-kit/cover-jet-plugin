package io.github.gwkit.coverjet.gradle.agent

import io.github.gwkit.coverjet.gradle.CoverJetExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import java.io.File

const val AGENT_CONFIG_NAME = "coverJetAgent"

internal fun Project.registerAgentConfigWithDependency(
    extension: CoverJetExtension,
): Provider<File> {
    val agentConfig: Provider<Configuration> = project.configurations.register(AGENT_CONFIG_NAME) { config ->
        dependencies.add(config.name, intellijAgentDep(extension.intellijCoverageVersion))
    }
    return agentConfig.map { it.singleFile }
}

private fun intellijAgentDep(version: Provider<String>): Provider<String> = version.map {
    "org.jetbrains.intellij.deps:intellij-coverage-agent:$it"
}
