package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.core.Package
import scala.collection.JavaConversions._
import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.core.logging.TheLogger

class DesignQualityDetectorsRunner(val detectors: java.util.List[Detector]) {

  //def this(theDetectors:Detector*) = this(List(theDetectors))

  def execute(packages: List[Package], designQualityCheckConfiguration: DesignQualityCheckConfiguration) = {

    val checkResults = scala.collection.mutable.ListBuffer[CheckResult]()
    val checkInput = new CheckInput(packages, designQualityCheckConfiguration)
    detectors.filter(_.isWanted(designQualityCheckConfiguration.expectations))
      .foreach(detector => {
        runDetector(checkResults, checkInput, detector)
      })
    new DesignQualityCheckResults(checkResults)
  }

  private def runDetector(checkResults: scala.collection.mutable.ListBuffer[CheckResult], checkInput: CheckInput, detector: Detector) = {
    try {
      TheLogger.info(detector + " is running");
      val checkResult = detector.analyze(checkInput);
      checkResults.add(checkResult);
    } catch {
      case unwantedException: RuntimeException => TheLogger.error(unwantedException.getMessage());
    }
  }

}