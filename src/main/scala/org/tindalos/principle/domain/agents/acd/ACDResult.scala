package org.tindalos.principle.domain.agents.acd

import org.tindalos.principle.domain.expectations.{DoubleExpectation, PackageCoupling}
import org.tindalos.principle.domain.agentscore.AnalysisResult

case class ACDResult(
    cumulatedComponentDependency: Int,
    numOfComponents: Int,
    packageCoupling: PackageCoupling) extends AnalysisResult {

  val acd = cumulatedComponentDependency.toDouble / numOfComponents.toDouble
  val rAcd = acd / numOfComponents.toDouble
  val nCcd = acd / numOfComponents.toDouble

  override def expectationsFailed() =
    greaterIfExists(rAcd, packageCoupling.racd) ||
    greaterIfExists(nCcd, packageCoupling.nccd)

  private def greaterIfExists(actual: Double, expectation: DoubleExpectation) =
    if (expectation == null || expectation.threshold == Double.NaN) false
    else actual > expectation.threshold

  def getRACDThreshold(): Double =
    if (packageCoupling.racd == null) 999d
    else packageCoupling.racd.threshold
}