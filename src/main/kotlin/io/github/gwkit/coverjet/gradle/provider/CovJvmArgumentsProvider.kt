package io.github.gwkit.coverjet.gradle.provider

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.process.CommandLineArgumentProvider
import java.io.Serializable
import javax.inject.Inject

internal class CovJvmArgumentsProvider @Inject constructor(
    private val file: Provider<RegularFile>,
) : CommandLineArgumentProvider, Serializable {

    override fun asArguments(): MutableIterable<String> {
        return file.get().asFile.readLines().toMutableList()
    }
}
