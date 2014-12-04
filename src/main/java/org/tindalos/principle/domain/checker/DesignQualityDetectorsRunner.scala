package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.logging.TheLogger
import org.tindalos.principle.domain.core.{DesignQualityCheckConfiguration, Package}
import org.tindalos.principle.domain.coredetector.{CheckInput, Detector}

object DesignQualityDetectorsRunner {

  def buildDetectorsRunner(detectors: List[Detector]) =

    (packages: List[Package], designQualityCheckConfiguration: DesignQualityCheckConfiguration) => {

      val checkInput = new CheckInput(packages, designQualityCheckConfiguration)

      val checkResults = for (detector <- detectors
                              if detector.isWanted(designQualityCheckConfiguration.expectations))
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