import org.gradle.api.plugins.jvm.JvmTestSuite

plugins {
    java
    kotlin("jvm") version "2.3.0"
    id("io.github.gw-kit.cover-jet")
    `java-test-fixtures`
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "io.github.gw-kit.cover-jet")

    repositories {
        mavenCentral()
    }

    testing.suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("5.13.4")
        }
        val intTest by registering(JvmTestSuite::class) {
            useJUnitJupiter("5.13.4")
            dependencies {
                implementation(project())
            }
        }
    }
}

