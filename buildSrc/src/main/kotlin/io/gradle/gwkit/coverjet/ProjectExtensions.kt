package io.gradle.gwkit.coverjet
import org.gradle.accessors.dm.LibrariesForDeps
import org.gradle.api.Project

internal val Project.libDeps: LibrariesForDeps
    get() = extensions.getByName("deps") as LibrariesForDeps
