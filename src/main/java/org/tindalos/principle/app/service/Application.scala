package org.tindalos.principle.app.service

import org.tindalos.principle.domain.checker.DesignQualityCheckService
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.resultprocessing.reporter.{DesignQualityCheckResultsReporter, Printer}

/*
This is the app entry point. Side effects can happen only here in this layer, underneath the code must be pure.
 */
class Application(designQualityCheckService: DesignQualityCheckService,
                  designQualityCheckResultsReporter: DesignQualityCheckResultsReporter,
                  inputValidator: InputValidator) {

  def run(designQualityCheckConfiguration: DesignQualityCheckConfiguration, printer: Printer) = {

    val (success, msg) = inputValidator.validate(designQualityCheckConfiguration)

    if (success) {

      val checkResults = designQualityCheckService.analyze(designQualityCheckConfiguration)

      val reports = designQualityCheckResultsReporter.getReports(checkResults)

      reports.foreach({ report =>
        if (report._2) printer.printWarning(report._1)
        else printer.printInfo(report._1)
      })

      (!checkResults.expectationsFailed, "Expectations failed")

    } else (success, msg)
  }
}