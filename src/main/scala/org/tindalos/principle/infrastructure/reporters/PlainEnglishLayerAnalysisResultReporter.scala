package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.app.reporters.LayerAnalysisResultReporter
import org.tindalos.principle.domain.analyzers.layering.{LayerReference, LayerViolationsResult}
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

import scala.collection.JavaConverters._

class PlainEnglishLayerAnalysisResultReporter extends LayerAnalysisResultReporter {

  def report(result: LayerViolationsResult): AnalysisResultsReporter.Report = {
    val layerReferences = result.violations.asScala
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tLayering violations (" + layerReferences.size + " of allowed " + result.threshold + " )\t")
    sb.append("\n" + sectionLine + "\n")

    def layerRefToStr(lr:LayerReference) = lr.referrer + " -> " + lr.referee
    if (layerReferences.isEmpty) sb.append("No violations.\n")
    else layerReferences.foreach(layerReference => sb.append(layerRefToStr(layerReference) + "\n"))
    
    sb.append(sectionLine + "\n")
    sb.toString()
  }


}