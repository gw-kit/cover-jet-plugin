package io.github.gwkit.coverjet

import org.gradle.api.Action

operator fun <T : Any> Action<in T>.invoke(target: T) = execute(target)
