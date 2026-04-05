error id: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/domain/analyzers/adp/CycleDetector.scala:Set
file://<WORKSPACE>/src/main/scala/org/tindalos/principle/domain/analyzers/adp/CycleDetector.scala
empty definition using pc, found symbol in pc: 
semanticdb not found

found definition using fallback; symbol Set
offset: 823
uri: file://<WORKSPACE>/src/main/scala/org/tindalos/principle/domain/analyzers/adp/CycleDetector.scala
text:
```scala
package org.tindalos.principle.domain.analyzers.adp

import org.tindalos.principle.domain.plan.AnalysisInput
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.packages.PackageReference
import org.tindalos.principle.domain.core.{Cycle, Package, PackageStructureBuilder}

import java.util
import scala.collection.JavaConverters._

class CycleDetector(packageStructureBuilder: PackageStructureBuilder) extends Analyzer {

    override def analyze(input: AnalysisInput) = {

      val basePackage = packageStructureBuilder.build(input.packages().asInstanceOf[java.util.List[Package]], input.analysisPlan().basePackage)

      val references = basePackage.toMap()

      var cycles = new util.HashMap[PackageReference, util.@@Set[Cycle]]()

      var sortedByAfferents = references.asScala.values.toList.sortBy(_.getMetrics().afferentCoupling)

      if (basePackage.getMetrics().afferentCoupling == 0) {
        sortedByAfferents = sortedByAfferents.filterNot(_ equals basePackage)
      }

      while (sortedByAfferents.nonEmpty) {
        val cyclesInSubgraph = sortedByAfferents.head.detectCycles(references)
        cycles = new util.HashMap[PackageReference, util.Set[Cycle]](cyclesInSubgraph.mergeBreakingPoints2(cycles))
        sortedByAfferents = sortedByAfferents.filterNot(cyclesInSubgraph.investigatedPackages.contains(_))
      }

      new ADPResult(cycles, input.packageCouplingExpectations().flatMap(_.adp()).get)
    }

    override def isEnabled(expectations: Constraints) = expectations.packageCoupling.flatMap(pc => pc.adp()).isPresent
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 