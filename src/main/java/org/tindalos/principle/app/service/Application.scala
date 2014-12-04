package org.tindalos.principle.app.service

import org.tindalos.principle.domain.checker.DesignQualityCheckResults
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.resultprocessing.reporter.Printer

/*
This is the app entry point. Side effects can happen only here in this layer, underneath the code must be pure.
 */
object Application {

  def buildApplication(checkDesignQuality: DesignQualityCheckConfiguration => DesignQualityCheckResults,
                       makeReports: DesignQualityCheckResults => List[(String, Boolean)],
                       validateInput: DesignQualityCheckConfiguration => (Boolean, String)) =

    (designQualityCheckConfiguration: DesignQualityCheckConfiguration, printer: Printer) => {

      val (success, msg) = validateInput(designQualityCheckConfiguration)

      def printReport(report: (String, Boolean)) =
        if (report._2)
          printer.printWarning(report._1)
        else
          printer.printInfo(report._1)


      if (success) {

        val checkResults = checkDesignQuality(designQualityCheckConfiguration)

        makeReports(checkResults).foreach(printReport)

        (!checkResults.expectationsFailed, "Expectations failed")

      } else (success, msg)

    }

}