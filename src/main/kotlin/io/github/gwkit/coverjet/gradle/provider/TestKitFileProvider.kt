package io.github.gwkit.coverjet.gradle.provider

import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.process.CommandLineArgumentProvider
import javax.inject.Inject

internal class TestKitFileProvider @Inject constructor(
    private val testKitFile: Provider<RegularFile>,
) : CommandLineArgumentProvider {

    override fun asArguments(): MutableIterable<String> {
        val jvmProperty: String = testKitFile
            .map { it.asFile.absolutePath }
            .map { path -> "-D$TEST_KIT_FILE=$path" }
            .get()
        return mutableListOf(jvmProperty)
    }

    companion object {
        const val TEST_KIT_FILE = "io.github.gwkit.coverjet.test-kit"
    }
}
