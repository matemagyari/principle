package org.tindalos.principle.app.service

import org.tindalos.principle.domain.resultprocessing.reporter.DesignQualityCheckResultsReporter
import org.tindalos.principle.domain.checker.DesignQualityCheckService
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.resultprocessing.reporter.Printer
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException

class Application(val designQualityCheckService: DesignQualityCheckService,
  val designQualityCheckResultsReporter: DesignQualityCheckResultsReporter) {

  def doIt(designQualityCheckConfiguration: DesignQualityCheckConfiguration, printer: Printer) = {
    val checkResults = designQualityCheckService.analyze(designQualityCheckConfiguration)
    designQualityCheckResultsReporter.report(checkResults, printer);

    if (checkResults.failExpectations()) {
      throw new ThresholdTrespassedException()
    }
    checkResults
  }

}