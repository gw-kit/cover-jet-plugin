package io.github.gwkit.coverjet.gradle.util

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

internal fun Project.getSourceSet(
    name: String,
    configure: Action<SourceSet> = Action {},
): Provider<SourceSet>? {
    return findSourceSets()?.named(name, configure)
}

private fun Project.findSourceSets(): SourceSetContainer? {
    return extensions.findByName("sourceSets") as? SourceSetContainer
}
