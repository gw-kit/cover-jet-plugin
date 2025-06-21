import io.github.surpsg.deltacoverage.gradle.CoverageEntity

plugins {
    `jvm-project-conventions`
    id("java-gradle-plugin")
    id("com.gradle.plugin-publish")

    `functional-tests-conventions`
    `java-test-fixtures`

    `delta-coverage-conventions`
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(deps.mockk)

    functionalTestImplementation(testFixtures(project))

    testFixturesImplementation(deps.junitApi)
    testFixturesImplementation(deps.kotestAssertions)
    testFixturesImplementation(deps.mockk)
}

gradlePlugin {
    website.set("https://github.com/SurpSG/cover-jet-plugin")
    vcsUrl.set("https://github.com/SurpSG/cover-jet-plugin.git")

    plugins {
        create("CoverJetPlugin") {
            id = "io.github.gw-kit.cover-jet"
            displayName = "Cover Jet Plugin"
            description = "Plugin that collects coverage from tests"
            implementationClass = "io.github.gwkit.coverjet.gradle.CoverJetPlugin"
            tags.set(listOf("coverage", "jacoco", "report"))
        }
    }
}

val functionalTestTaskName = "functionalTest"
val functionalTestSuite: JvmTestSuite = testing.suites.getByName(functionalTestTaskName) as JvmTestSuite
configure<GradlePluginDevelopmentExtension> {
    testSourceSets(
        functionalTestSuite.sources,
        project.extensions.getByType(JavaPluginExtension::class).sourceSets.getByName("testFixtures")
    )
}

tasks.named("check") {
    dependsOn(
        provider {
            subprojects.map { it.tasks.named("check") }
        }
    )
}

deltaCoverageReport {
    view(JavaPlugin.TEST_TASK_NAME) {
        enabled = false // Temporary disable
        violationRules.failIfCoverageLessThan(0.9)
    }
    view("functionalTest") {
        violationRules {
            failIfCoverageLessThan(0.6)
            CoverageEntity.BRANCH {
                minCoverageRatio = 0.5
            }
        }
    }
    view("aggregated") {
        enabled = false // Temporary disable
        violationRules.failIfCoverageLessThan(0.91)
    }
}
