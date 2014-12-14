package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.logging.TheLogger
import org.tindalos.principle.domain.core.{AnalysisPlan, Package}
import org.tindalos.principle.domain.coredetector.{Detector, AnalysisInput}

object DetectorsRunner {

  def buildDetectorsRunner(detectors: List[Detector]) =

    (input: AnalysisInput) => {

      val results = for {
        detector <- detectors
        if detector.isWanted(input.analysisPlan.expectations)
      } yield runDetector(input, detector)

      results.flatten
    }

  private def runDetector(input: AnalysisInput, detector: Detector) =
    try {
      TheLogger.info(detector + " is running.")
      Some(detector.analyze(input))
    } catch {
      case unwantedException: RuntimeException => TheLogger.error(unwantedException.getMessage)
        None
    }

}