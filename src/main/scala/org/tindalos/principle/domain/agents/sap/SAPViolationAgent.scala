package org.tindalos.principle.domain.agents.sap

import org.tindalos.principle.domain.agentscore.Agent
import org.tindalos.principle.domain.expectations.Checks
import org.tindalos.principle.domain.expectations.PackageCoupling
import org.tindalos.principle.domain.agentscore.AnalysisInput
import org.tindalos.principle.domain.core.Package

object SAPViolationAgent extends Agent {

  override def analyze(checkInput: AnalysisInput) = {
    val sapExpectation = checkInput.packageCouplingExpectations().get.sap
    val maxDistance = sapExpectation.maxDistance

    val outlierPackages = removeRootPackageIfEmpty(checkInput.packages).filter(_.distance > maxDistance)

    new SAPResult(outlierPackages, sapExpectation)
  }

  private def removeRootPackageIfEmpty(packages: List[Package]) = {
    val metrics = packages.head.getMetrics()
    if (metrics.abstractness == 0 && metrics.instability == 0) packages.tail
    else packages
  }

  override def isWanted(expectations: Checks) = expectations.packageCoupling.exists(_.sap != null)

}