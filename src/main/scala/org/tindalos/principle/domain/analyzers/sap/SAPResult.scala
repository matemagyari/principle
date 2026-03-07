package org.tindalos.principle.domain.analyzers.sap

import org.tindalos.principle.domain.AnalysisResult
import org.tindalos.principle.domain.constraints.SAP
import org.tindalos.principle.domain.core.packages.PackageWithMetrics

case class SAPResult(
    outlierPackages: List[PackageWithMetrics],
    sapExpectation: SAP) extends AnalysisResult {

  val threshold = sapExpectation.violationThreshold

  override def constraintViolated() = outlierPackages.length > threshold
}

