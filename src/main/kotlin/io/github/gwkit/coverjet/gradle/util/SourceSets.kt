package io.github.gwkit.coverjet.gradle.util

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer

internal fun Project.getSourceSet(
    name: String,
    configure: Action<SourceSet> = Action {},
): NamedDomainObjectProvider<SourceSet> {
    return (extensions.getByName("sourceSets") as SourceSetContainer)
        .named(name, configure)
}
