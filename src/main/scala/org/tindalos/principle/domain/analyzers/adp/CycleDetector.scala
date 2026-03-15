package org.tindalos.principle.domain.analyzers.adp

import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.packages.PackageReference
import org.tindalos.principle.domain.core.{Cycle, Package, PackageStructureBuilder}

import scala.collection.JavaConverters._

class CycleDetector(packageStructureBuilder: PackageStructureBuilder) extends Analyzer {

    override def analyze(input: AnalysisInput) = {

  val basePackage = packageStructureBuilder.build(input.packages().asScala.toList.asInstanceOf[List[Package]], input.analysisPlan().basePackage)

      val references = basePackage.toMap()

      var cycles = Map[PackageReference, Set[Cycle]]()

      var sortedByAfferents = references.values.toList.sortBy(_.getMetrics().afferentCoupling)

      if (basePackage.getMetrics().afferentCoupling == 0) {
        sortedByAfferents = sortedByAfferents.filterNot(_ equals basePackage)
      }

      while (sortedByAfferents.nonEmpty) {
        val cyclesInSubgraph = sortedByAfferents.head.detectCycles(references)
        cycles = cyclesInSubgraph.mergeBreakingPoints2(cycles)
        sortedByAfferents = sortedByAfferents.filterNot(cyclesInSubgraph.investigatedPackages.contains(_))
      }

      val javaCycles = cycles.map { case (k, v) => k -> v.asJava }.asJava
      new ADPResult(javaCycles, input.packageCouplingExpectations().flatMap(_.adp()).get)
    }

    override def isEnabled(expectations: Constraints) = expectations.packageCoupling.flatMap(pc => pc.adp()).isPresent
}