package org.tindalos.principle.domain.analyzers.acd

import org.tindalos.principle.domain.constraints.{DoubleExpectation, PackageCouplingConstraints}
import org.tindalos.principle.domain.agentscore.AnalysisResult

case class ACDResult(
    cumulatedComponentDependency: Int,
    numOfComponents: Int,
    packageCoupling: PackageCouplingConstraints) extends AnalysisResult {

  val acd = cumulatedComponentDependency.toDouble / numOfComponents.toDouble
  val rAcd = acd / numOfComponents.toDouble
  val nCcd = acd / numOfComponents.toDouble

  override def constraintViolated() =
    greaterIfExists(rAcd, packageCoupling.racd().map[DoubleExpectation](r => r).orElse(null)) ||
    greaterIfExists(nCcd, packageCoupling.nccd().map[DoubleExpectation](n => n).orElse(null))

  private def greaterIfExists(actual: Double, expectation: DoubleExpectation) =
    if (expectation == null || expectation.threshold == Double.NaN) false
    else actual > expectation.threshold

  def getRACDThreshold(): Double =
    packageCoupling.racd().map[java.lang.Double](r => r.threshold).orElse(999.0)
}