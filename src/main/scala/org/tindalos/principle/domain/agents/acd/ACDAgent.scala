package org.tindalos.principle.domain.agents.acd

import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.agentscore.{AnalysisInput, Agent}
import org.tindalos.principle.domain.expectations.{PackageCoupling, Checks}

object ACDAgent {
  
  def buildAgent(buildPackageStructure: (List[Package], String) => Package) = new Agent {

    override def analyze(checkInput: AnalysisInput) = {

      val basePackage = buildPackageStructure(checkInput.packages, checkInput.analysisPlan.basePackage)

      val referenceMap = basePackage.toMap()

      val relevantPackages =
        if (basePackage.isIsolated()) checkInput.packages.filterNot(_ equals basePackage)
        else checkInput.packages

      val cumulatedComponentDependency = relevantPackages
        .foldLeft(0)((acc, aPackage) => acc + aPackage.cumulatedDependencies(referenceMap).size + 1)

      new ACDResult(cumulatedComponentDependency, relevantPackages.length, checkInput.packageCouplingExpectations())
    }

    override def isWanted(expectations: Checks) = expectations.packageCoupling match {
      case packageCoupling: PackageCoupling =>
        (packageCoupling.acd != null) ||
          (packageCoupling.racd != null) ||
          (packageCoupling.nccd != null)
      case null => false
    }

  }
}