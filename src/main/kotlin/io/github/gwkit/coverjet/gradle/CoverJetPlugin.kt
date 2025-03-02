package io.github.gwkit.coverjet.gradle

import io.github.gwkit.coverjet.gradle.agent.registerAgentConfigWithDependency
import io.github.gwkit.coverjet.gradle.provider.CovJvmArgumentsProvider
import io.github.gwkit.coverjet.gradle.provider.TestKitFileProvider
import io.github.gwkit.coverjet.gradle.task.generateTestKitProperties
import io.github.gwkit.coverjet.gradle.task.registerGenCoverageAgentProperties
import io.github.gwkit.coverjet.gradle.util.getSourceSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.Test
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File

open class CoverJetPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        val extension: CoverJetExtension = extensions.create("coverJet", CoverJetExtension::class.java)

        val coverJetAgentProvider: Provider<File> = project.registerAgentConfigWithDependency(extension)

        configureTestTasks(coverJetAgentProvider)
    }

    private fun Project.configureTestTasks(
        covAgentProvider: Provider<File>,
    ) {
        tasks.withType(Test::class.java) { testTask ->

            val agentPropertiesProvider = registerGenCoverageAgentProperties(testTask.name)
            val jvmArgsProvider = CovJvmArgumentsProvider(covAgentProvider, agentPropertiesProvider)

            testTask.jvmArgumentProviders += jvmArgsProvider

            val generateTestKitPropTaskProvider = generateTestKitProperties(testTask.name, jvmArgsProvider) {
                dependsOn(agentPropertiesProvider)

                testTask.jvmArgumentProviders += TestKitFileProvider(destinationFile.asFile)
            }
            testTask.dependsOn(generateTestKitPropTaskProvider)

            getSourceSet(testTask.name) { sourceSet ->
                tasks.named(sourceSet.processResourcesTaskName, ProcessResources::class.java) {
                    it.from(generateTestKitPropTaskProvider)
                }

                configurations.named(sourceSet.runtimeOnlyConfigurationName) { runtimeOnlyConfig ->
                    runtimeOnlyConfig.dependencies += dependencyFactory.create(
                        generateTestKitPropTaskProvider.get().outputs.files
                    )
                }
            }
        }
    }
}
