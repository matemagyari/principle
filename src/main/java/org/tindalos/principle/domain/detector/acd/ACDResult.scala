package org.tindalos.principle.domain.detector.acd

import org.tindalos.principle.domain.expectations.PackageCoupling
import org.tindalos.principle.domain.coredetector.CheckResult
import org.tindalos.principle.domain.expectations.cumulativedependency.DoubleThresholder

class ACDResult(val cumulatedComponentDependency: Int, val numOfComponents: Int, val packageCoupling: PackageCoupling) extends CheckResult {

  val acd = cumulatedComponentDependency.toDouble / numOfComponents.toDouble
  val rAcd = acd / numOfComponents.toDouble
  val nCcd = acd / numOfComponents.toDouble

  def expectationsFailed() =
    greaterIfExists(acd, packageCoupling.getACD()) || 
    greaterIfExists(rAcd, packageCoupling.getRACD()) || 
    greaterIfExists(nCcd, packageCoupling.getNCCD())

  private def greaterIfExists(actual: Double, expectation: DoubleThresholder) =
    if (expectation == null || expectation.getThreshold() == null) false
    else actual > expectation.getThreshold()

  def getACDThreshold() =
    if (packageCoupling.getACD() == null) 999d
    else packageCoupling.getACD().getThreshold()

  def getRACDThreshold() =
    if (packageCoupling.getRACD() == null) 999d
    else packageCoupling.getRACD().getThreshold()
}