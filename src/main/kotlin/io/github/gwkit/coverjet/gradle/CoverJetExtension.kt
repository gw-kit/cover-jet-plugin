package io.github.gwkit.coverjet.gradle

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import javax.inject.Inject

open class CoverJetExtension @Inject constructor(
    objects: ObjectFactory,
) {

    @Input
    val intellijCoverageVersion: Property<String> = objects.property(String::class.java).convention("1.0.744")

    @Input
    val countHits: Property<Boolean> = objects.property(Boolean::class.java).convention(false)

    @Input
    val enableTracing: Property<Boolean> = objects.property(Boolean::class.java).convention(true)
}
