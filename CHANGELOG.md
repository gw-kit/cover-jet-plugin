# CoverJet Gradle plugin Changelog

## 0.1.1

- #23 Fixed configuration cache
- Updated Gradle to 8.14.2

## 0.1.0

- Simplified configuration by removing copy jvm coverage agent copy task.
- Fixed source set lookup when a project doesn't have applied java plugin. 
- Gradle test kit properties are propagated via system properties to test tasks
  ```kts
  val testKitFile = System.getProperty("io.github.gwkit.coverjet.test-kit.enabled")
  ```

## 0.0.1
- Initial release
