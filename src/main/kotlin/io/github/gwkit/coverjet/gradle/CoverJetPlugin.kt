package io.github.gwkit.coverjet.gradle

import io.github.gwkit.coverjet.gradle.agent.registerAgentConfigWithDependency
import io.github.gwkit.coverjet.gradle.provider.CovJvmArgumentsProvider
import io.github.gwkit.coverjet.gradle.provider.TestKitFileProvider
import io.github.gwkit.coverjet.gradle.task.CovAgentProperties
import io.github.gwkit.coverjet.gradle.task.CovJvmParameter
import io.github.gwkit.coverjet.gradle.task.GenTestKitProperties
import io.github.gwkit.coverjet.gradle.task.generateTestKitProperties
import io.github.gwkit.coverjet.gradle.task.registerCovJvmParameter
import io.github.gwkit.coverjet.gradle.task.registerGenCoverageAgentArgs
import io.github.gwkit.coverjet.gradle.util.getSourceSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File

open class CoverJetPlugin : Plugin<Project> {

    override fun apply(project: Project): Unit = with(project) {
        val extension: CoverJetExtension = extensions.create("coverJet", CoverJetExtension::class.java)

        val coverJetAgentJarProvider: Provider<File> = project.registerAgentConfigWithDependency(
            extension.intellijCoverageVersion
        )

        tasks.withType(Test::class.java) { testTask ->
            val covJvmArgProvider: TaskProvider<CovJvmParameter> = registerCovAgentJvmParamGenTask(
                testTask.name,
                coverJetAgentJarProvider,
            )
            testTask.applyCoverageAgentToJvmArgs(covJvmArgProvider)
            testTask.applyTestKit(covJvmArgProvider)
        }
    }

    private fun Project.registerCovAgentJvmParamGenTask(
        testTaskName: String,
        coverJetAgentJarProvider: Provider<File>,
    ): TaskProvider<CovJvmParameter> {
        val agentArgsProvider: TaskProvider<CovAgentProperties> = registerGenCoverageAgentArgs(
            testTaskName
        )
        val covJvmArgProvider: TaskProvider<CovJvmParameter> = registerCovJvmParameter(
            testTaskName,
            coverJetAgentJarProvider,
            agentArgsProvider,
        )
        return covJvmArgProvider
    }

    private fun Test.applyCoverageAgentToJvmArgs(
        covJvmArgProvider: TaskProvider<CovJvmParameter>
    ) {
        inputs.files(covJvmArgProvider)
        jvmArgumentProviders += CovJvmArgumentsProvider(
            covJvmArgProvider.flatMap { it.javaAgentParameters }
        )
    }

    private fun Test.applyTestKit(
        covJvmArgProvider: TaskProvider<CovJvmParameter>
    ) {
        val testTaskName: String = name
        val genTestKitPropsProvider: TaskProvider<GenTestKitProperties> = project.generateTestKitProperties(
            testTaskName,
            covJvmArgProvider,
        )
        inputs.files(genTestKitPropsProvider)

        jvmArgumentProviders += TestKitFileProvider(
            genTestKitPropsProvider.flatMap { it.javaAgentParametersFile }
        )

        project.getSourceSet(testTaskName) { sourceSet ->
            project.tasks.named(sourceSet.processResourcesTaskName, ProcessResources::class.java) {
                it.from(genTestKitPropsProvider)
            }

            project.configurations.named(sourceSet.runtimeOnlyConfigurationName) { runtimeOnlyConfig ->
                runtimeOnlyConfig.dependencies += project.dependencyFactory.create(
                    genTestKitPropsProvider.get().outputs.files
                )
            }
        }
    }
}
