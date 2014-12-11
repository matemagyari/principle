package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.logging.TheLogger
import org.tindalos.principle.domain.core.{AnalysisInput, Package}
import org.tindalos.principle.domain.coredetector.{PackagesAndExpectations, Detector}

object DetectorsRunner {

  def buildDetectorsRunner(detectors: List[Detector]) =

    (packages: List[Package], expectationsConfig: AnalysisInput) => {

      val checkInput = new PackagesAndExpectations(packages, expectationsConfig)

      val checkResults = for (detector <- detectors
                              if detector.isWanted(expectationsConfig.expectations))
                          yield runDetector(checkInput, detector)

      checkResults.flatten
    }

  private def runDetector(checkInput: PackagesAndExpectations, detector: Detector) =
    try {
      TheLogger.info(detector + " is running.")
      Some(detector.analyze(checkInput))
    } catch {
      case unwantedException: RuntimeException => TheLogger.error(unwantedException.getMessage)
        None
    }

}