package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.detector.layering.{LayerReference, LayerViolationsResult}
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

object LayerViolationsReporter {

  def report(result: LayerViolationsResult):AnalysisResultsReporter.Report = {
    val layerReferences = result.violations
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tLayering violations (" + layerReferences.length + " of allowed " + result.threshold + " )\t")
    sb.append("\n" + sectionLine + "\n")

    def layerRefToStr(lr:LayerReference) = lr.referrer + " -> " + lr.referee
    if (layerReferences.isEmpty) sb.append("No violations.\n")
    else layerReferences.foreach(layerReference => sb.append(layerRefToStr(layerReference) + "\n"))
    
    sb.append(sectionLine + "\n")
    sb.toString()
  }

  //override def getType() = classOf[LayerViolationsResult]

}