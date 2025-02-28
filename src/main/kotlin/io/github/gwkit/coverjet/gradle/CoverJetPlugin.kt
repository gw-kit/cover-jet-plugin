package io.github.gwkit.coverjet.gradle

import io.github.gwkit.coverjet.gradle.task.CovAgentTask
import io.github.gwkit.coverjet.gradle.task.generateTestKitProperties
import io.github.gwkit.coverjet.gradle.task.registerCopyCoverageAgentTask
import io.github.gwkit.coverjet.gradle.util.getSourceSet
import io.github.gwkit.coverjet.gradle.provider.CovJvmArgumentsProvider
import io.github.gwkit.coverjet.gradle.task.registerGenCoverageAgentProperties
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.testing.Test
import org.gradle.language.jvm.tasks.ProcessResources
import java.util.concurrent.ConcurrentHashMap

open class CoverJetPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        val extension: CoverJetExtension = extensions.create("coverJet", CoverJetExtension::class.java)

        val coverJetAgentConfig: NamedDomainObjectProvider<Configuration> =
            project.registerAgentConfigWithDependency(extension)

        val covAgent: TaskProvider<CovAgentTask> = project.registerCopyCoverageAgentTask(
            extension = extension,
            agentConfig = coverJetAgentConfig,
        )

        configureTestTasks(covAgent)
    }

    private fun Project.registerAgentConfigWithDependency(
        extension: CoverJetExtension,
    ): NamedDomainObjectProvider<Configuration> {
        return project.configurations.register("coverJetAgent") { config ->
            dependencies.add(config.name, intellijAgentDep(extension.intellijCoverageVersion))
        }
    }

    private fun Project.configureTestTasks(
        covAgentProvider: TaskProvider<CovAgentTask>,
    ) {
        val testTasks = mutableSetOf<String>()
        val testTaskToProps = ConcurrentHashMap<String, Task>()
        tasks.withType(Test::class.java) { testTask ->
            testTasks += testTask.name

            val agentPropertiesProvider = registerGenCoverageAgentProperties(testTask.name)
            val jvmArgsProvider = CovJvmArgumentsProvider(covAgentProvider, agentPropertiesProvider)

            testTask.jvmArgumentProviders += jvmArgsProvider

            val generateTestKitPropTaskProvider = generateTestKitProperties(testTask.name, jvmArgsProvider) {
                dependsOn(covAgentProvider, agentPropertiesProvider)
                testTaskToProps[testTask.name] = this
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

    private fun intellijAgentDep(version: Provider<String>): Provider<String> = version.map {
        "org.jetbrains.intellij.deps:intellij-coverage-agent:$it"
    }
}
