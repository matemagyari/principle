package org.tindalos.principle.domain.analyzers.sap

import org.tindalos.principle.domain.agentscore.AnalysisResult
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.constraints.SAP

case class SAPResult(
    outlierPackages: List[Package],
    sapExpectation: SAP) extends AnalysisResult {

  val threshold = sapExpectation.violationThreshold

  override def expectationsFailed() = outlierPackages.length > threshold
}

