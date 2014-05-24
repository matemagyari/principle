package org.tindalos.principle.infrastructure.reporters

import org.tindalos.principle.domain.coredetector.ViolationsReporter
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult

class LayerViolationsReporter extends ViolationsReporter[LayerViolationsResult] {

  override def report(result: LayerViolationsResult) = {
    val layerReferences = result.violations
    val sectionLine = "=============================================================="
    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tLayering violations (" + layerReferences.length + " of allowed " + result.threshold + " )\t")
    sb.append("\n" + sectionLine + "\n")

    if (layerReferences.isEmpty) sb.append("No violations.\n")
    else layerReferences.foreach(layerReference => sb.append(layerReference + "\n"))
    
    sb.append(sectionLine + "\n")
    sb.toString()
  }

  override def getType() = classOf[LayerViolationsResult]

}