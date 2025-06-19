package io.github.gwkit.coverjet.gradle.provider

import org.gradle.api.provider.Provider
import org.gradle.process.CommandLineArgumentProvider
import javax.inject.Inject

internal class CovJvmArgumentsProvider @Inject constructor(
    private val covJvmParameterLines: Provider<List<String>>,
) : CommandLineArgumentProvider {

    override fun asArguments(): MutableIterable<String> =
        covJvmParameterLines.get().toMutableList()
}
