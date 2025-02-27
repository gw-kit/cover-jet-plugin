plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = "cover-jet"

dependencyResolutionManagement {
    versionCatalogs {
        create("deps") {
            from(files("gradle/deps.versions.toml"))
        }
    }
}

buildCache {
    local.isEnabled = true
}
