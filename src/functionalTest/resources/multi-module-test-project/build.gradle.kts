import org.gradle.api.plugins.jvm.JvmTestSuite

plugins {
    java
    kotlin("jvm") version "2.1.20"
    id("io.github.gw-kit.cover-jet")
    `java-test-fixtures`
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "kotlin")
    apply(plugin = "org.jetbrains.kotlinx.kover")

    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:5.13.1"))
        testImplementation("org.junit.jupiter:junit-jupiter")
    }

    testing.suites {
        val intTest by registering(JvmTestSuite::class) {
            useJUnitJupiter()
            dependencies {
                implementation(project())
                implementation(platform("org.junit:junit-bom:5.13.1"))
                implementation("org.junit.jupiter:junit-jupiter")
            }
        }
    }
}

