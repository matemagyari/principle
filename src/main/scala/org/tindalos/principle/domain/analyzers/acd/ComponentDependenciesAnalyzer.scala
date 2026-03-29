package org.tindalos.principle.domain.analyzers.acd

import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.{Package, PackageStructureBuilder}
import scala.collection.JavaConverters._

class ComponentDependenciesAnalyzer(packageStructureBuilder: PackageStructureBuilder) extends Analyzer {

    override def analyze(checkInput: AnalysisInput): ComponentDependenciesResult = {

      val packages = checkInput.packages().asScala.toList
      val basePackage = packageStructureBuilder.build(checkInput.packages().asInstanceOf[java.util.List[Package]], checkInput.analysisPlan().basePackage)

      val referenceMap = basePackage.toMap()

      val relevantPackages =
        if (basePackage.getMetrics().isIsolated()) packages.filterNot(_ equals basePackage)
        else packages

      val cumulatedComponentDependency = relevantPackages
        .foldLeft(0) { (acc, aPackage) ⇒
          acc + aPackage.asInstanceOf[Package].cumulatedDependencies(referenceMap).size + 1
        }

      //todo - remove .get
      new ComponentDependenciesResult(cumulatedComponentDependency, relevantPackages.length, checkInput.packageCouplingExpectations().get)
    }

    override def isEnabled(expectations: Constraints): Boolean =
      Option(expectations.packageCoupling().orElse(null))
        .exists(pc => pc.acd().isPresent || pc.racd().isPresent || pc.nccd().isPresent)
}