import org.gradle.api.plugins.jvm.JvmTestSuite

plugins {
    java
    kotlin("jvm") version "1.9.25"
    id("io.github.gw-kit.cover-jet")
}

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

testing.suites {
    val test by getting(JvmTestSuite::class) {
        useJUnitJupiter()
        dependencies {
            implementation(platform("org.junit:junit-bom:5.11.4"))
            implementation("org.junit.jupiter:junit-jupiter")
        }
    }
    val intTest by registering(JvmTestSuite::class) {
        useJUnitJupiter()
        dependencies {
            implementation(project())
            implementation(platform("org.junit:junit-bom:5.11.4"))
            implementation("org.junit.jupiter:junit-jupiter")
        }
    }
}
