package org.tindalos.principle.domain.agents.sap

import org.tindalos.principle.domain.agentscore.AnalysisResult
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.expectations.SAP

import scala.collection.immutable.Seq

case class SAPResult(
    outlierPackages: Seq[Package],
    sapExpectation: SAP) extends AnalysisResult {

  val threshold = sapExpectation.violationsThreshold

  override def expectationsFailed() = outlierPackages.length > threshold
}

