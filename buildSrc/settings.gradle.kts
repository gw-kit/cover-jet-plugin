rootProject.name = "cover-jet-conventions"

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://maven.pkg.github.com/gw-kit/cover-jet-plugin")
            credentials {
                username = extra.properties["GH_USER"]?.toString() ?: System.getenv("GH_USER")
                password = extra.properties["GH_TOKEN"]?.toString() ?: System.getenv("GH_TOKEN")
            }
        }
    }
    versionCatalogs {
        create("deps") {
            from(files("../gradle/deps.versions.toml"))
        }
    }
}
