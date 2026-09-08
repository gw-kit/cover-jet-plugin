import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    kotlin("jvm") version "2.4.20"
    id("io.github.gw-kit.cover-jet")
}

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events(TestLogEvent.SKIPPED, TestLogEvent.FAILED, TestLogEvent.PASSED)
        showStandardStreams = true
    }
}

testing.suites {
    val test by getting(JvmTestSuite::class) {
        useJUnitJupiter()
        dependencies {
            implementation(platform("org.junit:junit-bom:6.1.3"))
            implementation("org.junit.jupiter:junit-jupiter")
        }
    }
    val intTest by registering(JvmTestSuite::class) {
        useJUnitJupiter()
        dependencies {
            implementation(project())
            implementation(platform("org.junit:junit-bom:6.1.3"))
            implementation("org.junit.jupiter:junit-jupiter")
        }
    }
}
