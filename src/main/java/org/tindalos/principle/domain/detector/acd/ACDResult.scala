package org.tindalos.principle.domain.detector.acd

import org.tindalos.principle.domain.expectations.PackageCoupling
import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.expectations.cumulativedependency.DoubleThresholder

class ACDResult(val cumulatedComponentDependency: Int, val numOfComponents: Int, val packageCoupling: PackageCoupling) extends CheckResult {

  val acd = cumulatedComponentDependency.toDouble / numOfComponents.toDouble
  val rAcd = acd / numOfComponents.toDouble
  val nCcd = acd / numOfComponents.toDouble

  def expectationsFailed() =
    greaterIfExists(acd, packageCoupling.acd) || 
    greaterIfExists(rAcd, packageCoupling.racd) || 
    greaterIfExists(nCcd, packageCoupling.nccd)

  private def greaterIfExists(actual: Double, expectation: DoubleThresholder) =
    if (expectation == null || expectation.threshold == Double.NaN) false
    else actual > expectation.threshold

  def getACDThreshold() =
    if (packageCoupling.acd == null) 999d
    else packageCoupling.acd.threshold

  def getRACDThreshold() =
    if (packageCoupling.racd == null) 999d
    else packageCoupling.racd.threshold
}