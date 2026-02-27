package org.tindalos.principle.domain.analyzers.acd

import org.tindalos.principle.domain.agentscore.{AnalysisInput, Analyzer}
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.PackageStructureBuilder

class ComponentDependenciesAnalyzer(packageStructureBuilder: PackageStructureBuilder) extends Analyzer {

    override def analyze(checkInput: AnalysisInput): ComponentDependenciesResult = {

      val basePackage = packageStructureBuilder.build(checkInput.packages, checkInput.analysisPlan.basePackage)

      val referenceMap = basePackage.toMap()

      val relevantPackages =
        if (basePackage.isIsolated()) checkInput.packages.filterNot(_ equals basePackage)
        else checkInput.packages

      val cumulatedComponentDependency = relevantPackages
        .foldLeft(0) { (acc, aPackage) ⇒
          acc + aPackage.cumulatedDependencies(referenceMap).size + 1
        }

      //todo - remove .get
      new ComponentDependenciesResult(cumulatedComponentDependency, relevantPackages.length, checkInput.packageCouplingExpectations().get)
    }

    override def isEnabled(expectations: Constraints): Boolean =
      Option(expectations.packageCoupling().orElse(null))
        .exists(pc => pc.acd().isPresent || pc.racd().isPresent || pc.nccd().isPresent)
}