package org.tindalos.principle.app.service

import org.tindalos.principle.domain.checker.DesignQualityCheckResults
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.resultprocessing.reporter.Printer

/*
This is the app entry point. Side effects can happen only here in this layer, underneath the code must be pure.
 */
object Application {

  def buildApplication(designQualityCheck: DesignQualityCheckConfiguration => DesignQualityCheckResults,
                       designQualityCheckResultsReporter: DesignQualityCheckResults => List[(String, Boolean)],
                       inputValidator: DesignQualityCheckConfiguration => (Boolean, String)) =

    (designQualityCheckConfiguration: DesignQualityCheckConfiguration, printer: Printer) => {

      val (success, msg) = inputValidator(designQualityCheckConfiguration)

      if (success) {

        val checkResults = designQualityCheck(designQualityCheckConfiguration)

        val reports = designQualityCheckResultsReporter(checkResults)

        reports.foreach({ report =>
          if (report._2) printer.printWarning(report._1)
          else printer.printInfo(report._1)
        })

        (!checkResults.expectationsFailed, "Expectations failed")

      } else (success, msg)

    }

}