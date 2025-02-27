package io.github.gwkit.coverjet.gradle

import org.gradle.api.file.FileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import javax.inject.Inject

open class DeltaCoverageConfiguration @Inject constructor(
    objectFactory: ObjectFactory,
) {

    @Optional
    @InputFiles
    var classesDirs: FileCollection? = null

    @Optional
    @InputFiles
    var sources: FileCollection? = null

    @Input
    val excludeClasses: ListProperty<String> = objectFactory
        .listProperty(String::class.javaObjectType)
        .convention(emptyList())
}
