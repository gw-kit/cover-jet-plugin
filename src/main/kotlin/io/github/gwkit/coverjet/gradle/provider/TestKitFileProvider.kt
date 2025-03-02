package io.github.gwkit.coverjet.gradle.provider

import org.gradle.api.provider.Provider
import org.gradle.process.CommandLineArgumentProvider
import java.io.File
import javax.inject.Inject

internal class TestKitFileProvider @Inject constructor(
    private val testKitFile: Provider<File>,
) : CommandLineArgumentProvider {

    override fun asArguments(): MutableIterable<String> {
        val jvmProperty: String = testKitFile
            .map { it.absolutePath }
            .map { path -> "-D$TEST_KIT_FILE=$path" }
            .get()
        return mutableListOf(jvmProperty)
    }

    companion object {
        const val TEST_KIT_FILE = "io.github.gwkit.coverjet.test-kit"
    }
}
