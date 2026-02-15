package org.tindalos.principle.domain.analyzers.acd

import org.tindalos.principle.domain.agentscore.{Analyzer, AnalysisInput}
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.Package

object ACDAgent {
  
  def buildAgent(buildPackageStructure: (List[Package], String) => Package) = new Analyzer {

    override def analyze(checkInput: AnalysisInput): ACDResult = {

      val basePackage = buildPackageStructure(checkInput.packages, checkInput.analysisPlan.basePackage)

      val referenceMap = basePackage.toMap()

      val relevantPackages =
        if (basePackage.isIsolated()) checkInput.packages.filterNot(_ equals basePackage)
        else checkInput.packages

      val cumulatedComponentDependency = relevantPackages
        .foldLeft(0) { (acc, aPackage) ⇒
          acc + aPackage.cumulatedDependencies(referenceMap).size + 1
        }

      //todo - remove .get
      new ACDResult(cumulatedComponentDependency, relevantPackages.length, checkInput.packageCouplingExpectations().get)
    }

    override def isWanted(expectations: Constraints) = {
      if (expectations.packageCoupling().isPresent) {
        val packageCoupling = expectations.packageCoupling().get()
        packageCoupling.acd().isPresent ||
            packageCoupling.racd().isPresent ||
            packageCoupling.nccd().isPresent
      } else {
        false
      }
    }
  }
}