package org.tindalos.principle.domain.agents.acd

import org.tindalos.principle.domain.agentscore.{Agent, AnalysisInput}
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.expectations.Checks

import scala.collection.immutable.Seq

object ACDAgent {
  
  def buildAgent(buildPackageStructure: (Seq[Package], String) ⇒ Package) = new Agent {

    override def analyze(checkInput: AnalysisInput) = {

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

    override def isWanted(expectations: Checks) = expectations.packageCoupling
        .exists { packageCoupling ⇒
          (packageCoupling.acd != null) ||
              (packageCoupling.racd != null) ||
              (packageCoupling.nccd != null)
        }
  }
}