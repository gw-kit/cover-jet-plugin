plugins {
    `gradle-plugin-conventions`
    `delta-coverage-conventions`
}

repositories {
    mavenCentral()
}

gradlePlugin {
    website.set("https://github.com/SurpSG/cover-jet-plugin")
    vcsUrl.set("https://github.com/SurpSG/cover-jet-plugin.git")

    plugins {
        create("CoverJetPlugin") {
            id = "io.github.gw-kit.cover-jet-plugin"
            displayName = "Cover Jet Plugin"
            description = "Plugin that collects coverage from tests"
            implementationClass = "io.github.gwkit.coverjet.gradle.CoverJetPlugin"
            tags.set(listOf("coverage", "jacoco", "report"))
        }
    }
}

tasks.named("check") {
    dependsOn(
        provider {
            subprojects.map { it.tasks.named("check") }
        }
    )
}
