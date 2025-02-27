package io.github.surpsg.deltacoverage

import io.github.surpsg.deltacoverage.config.DeltaCoverageConfig
import io.github.surpsg.deltacoverage.config.ReportConfig
import io.github.surpsg.deltacoverage.config.ReportsConfig
import io.github.surpsg.deltacoverage.diff.FileDiffSource
import io.github.surpsg.deltacoverage.report.DeltaReportFacadeFactory
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Paths


class Testic {

//    @Disabled
    @Test
    fun `should `() {

        val engine = CoverageEngine.JACOCO
        val deltaReportFacade = DeltaReportFacadeFactory.buildFacade(engine)

        deltaReportFacade.generateReports(
            Paths.get("/Users/sergnat/ideaProjects/delta-coverage-plugin-org/1.json"),
            DeltaCoverageConfig {
                viewName = "view1"
                coverageEngine = engine
                diffSource = FileDiffSource(
                    "/Users/sergnat/ideaProjects/delta-coverage-plugin-org/1.diff"
                )
                reportsConfig = ReportsConfig {
                    baseReportDir = "/Users/sergnat/ideaProjects/delta-coverage-plugin-org/build/new/"
                    html = ReportConfig {
                        enabled = true
                        outputFileName = "jacoco-report.html"
                    }
                    console = ReportConfig {
                        enabled = true
                        outputFileName = "1"
                    }
                }
                binaryCoverageFiles.add(
                    File(
                        "/Users/sergnat/ideaProjects/delta-coverage-plugin-org/delta-coverage-core/build/test.ic"
                    )
                )
                classFiles.add(
                    File(
                        "/Users/sergnat/ideaProjects/a911-svc/video/build/classes/kotlin/main"
                    )
                )
                sourceFiles.add(
                    File(
                        "/Users/sergnat/ideaProjects/a911-svc/video/src/main/kotlin"
                    )
                )
            },
        )

    }
}
