error id: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/domain/analyzers/acd/ComponentDependenciesAnalyzer.scala:AnalysisInput
file://<WORKSPACE>/src/main/scala/org/tindalos/principle/domain/analyzers/acd/ComponentDependenciesAnalyzer.scala
empty definition using pc, found symbol in pc: 
semanticdb not found

found definition using fallback; symbol AnalysisInput
offset: 96
uri: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/domain/analyzers/acd/ComponentDependenciesAnalyzer.scala
text:
```scala
package org.tindalos.principle.domain.analyzers.acd

import org.tindalos.principle.domain.Analys@@isInput
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
```


#### Short summary: 

empty definition using pc, found symbol in pc: 