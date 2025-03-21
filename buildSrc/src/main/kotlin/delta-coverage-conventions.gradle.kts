import io.github.surpsg.deltacoverage.gradle.CoverageEngine
import io.github.surpsg.deltacoverage.gradle.CoverageEntity

plugins {
    base
    id("io.github.gw-kit.delta-coverage")
}

deltaCoverageReport {
    coverage.engine = CoverageEngine.INTELLIJ

    diffSource.byGit {
        diffBase = project.properties["diffBase"]?.toString() ?: "refs/remotes/origin/main"
        useNativeGit = true
    }

    reports {
        html = true
        xml = true
        markdown = true
    }

    view(JavaPlugin.TEST_TASK_NAME) {
        enabled = false // Temporary disable
        violationRules.failIfCoverageLessThan(0.9)
    }
    view("functionalTest") {
        coverageBinaryFiles = files(project.layout.buildDirectory.file("coverage/functionalTest.ic"))
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
