package io.github.gwkit.coverjet.gradle.provider

import io.github.gwkit.coverjet.gradle.task.readJavaAgentParameter
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.process.CommandLineArgumentProvider
import javax.inject.Inject

internal class CovJvmArgumentsProvider @Inject constructor(
    private val file: Provider<RegularFile>,
) : CommandLineArgumentProvider {

    override fun asArguments(): MutableIterable<String> {
        return file.readJavaAgentParameter().get().toMutableList()
    }
}
