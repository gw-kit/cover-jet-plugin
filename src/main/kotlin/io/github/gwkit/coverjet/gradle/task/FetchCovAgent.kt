package io.github.gwkit.coverjet.gradle.task

import io.github.gwkit.coverjet.gradle.CoverJetExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskProvider
import java.io.File
import javax.inject.Inject

internal fun Project.registerCopyCoverageAgentTask(
    extension: CoverJetExtension,
    agentConfig: Provider<Configuration>,
): TaskProvider<CovAgentTask> {
    return rootProject.tasks.register("intellijCovAgent", CovAgentTask::class.java) { task ->
        task.from(agentConfig)
        task.into(
            extension.intellijCoverageVersion.flatMap { agentVersion ->
                project.layout.buildDirectory.file("coverage-agent/${agentVersion}")
            }
        )
    }
}

internal abstract class CovAgentTask @Inject constructor(
    objects: ObjectFactory,
) : Copy() {
    init {
        description = "Copy IntelliJ coverage agent to build directory"
    }

    @Internal
    val agentFile: Property<File> = objects.property(File::class.java).convention(
        project.provider {
            outputs.files.asFileTree.find { it.extension == "jar" }
        }
    )
}
