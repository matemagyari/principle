package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.logging.TheLogger

class DesignQualityDetectorsRunner(val detectors: List[Detector]) {

  //def this(theDetectors:Detector*) = this(List(theDetectors))

  def execute(packages: List[Package], designQualityCheckConfiguration: DesignQualityCheckConfiguration) = {

    val checkInput = new CheckInput(packages, designQualityCheckConfiguration)

    val checkResults = for (detector <- detectors if detector.isWanted(designQualityCheckConfiguration.expectations))
    yield runDetector(checkInput, detector)

    new DesignQualityCheckResults(checkResults.flatten)
  }

  private def runDetector(checkInput: CheckInput, detector: Detector) =
    try {
      TheLogger.info(detector + " is running.")
      Some(detector.analyze(checkInput))
    } catch {
      case unwantedException: RuntimeException => TheLogger.error(unwantedException.getMessage)
        None
    }

}