package org.tindalos.principle.domain.analyzers.adp

import org.tindalos.principle.domain.agentscore.{AnalysisInput, Analyzer}
import org.tindalos.principle.domain.constraints.Constraints
import org.tindalos.principle.domain.core.packages.PackageReference
import org.tindalos.principle.domain.core.{Cycle, PackageStructureBuilder}

object CycleDetector {

  private def toScalaOption[T](javaOptional: java.util.Optional[T]): Option[T] = {
    if (javaOptional.isPresent) Some(javaOptional.get()) else None
  }

  def buildAgent(packageStructureBuilder: PackageStructureBuilder) = new Analyzer {

    override def analyze(input: AnalysisInput) = {

      val basePackage = packageStructureBuilder.build(input.packages, input.analysisPlan.basePackage)

      val references = basePackage.toMap()

      var cycles = Map[PackageReference, Set[Cycle]]()

      var sortedByAfferents = references.values.toList.sortBy(_.getMetrics().afferentCoupling)

      if (basePackage.getMetrics().afferentCoupling == 0) {
        sortedByAfferents = sortedByAfferents.filterNot(_ equals basePackage)
      }

      while (!sortedByAfferents.isEmpty) {
        val cyclesInSubgraph = sortedByAfferents.head.detectCycles(references)
        cycles = cyclesInSubgraph.mergeBreakingPoints2(cycles)
        sortedByAfferents = sortedByAfferents.filterNot(cyclesInSubgraph.investigatedPackages.contains(_))
      }

      new ADPResult(cycles, input.packageCouplingExpectations().flatMap(pc => toScalaOption(pc.adp())).get)
    }

    override def isEnabled(expectations: Constraints) = expectations.packageCoupling.flatMap(pc => pc.adp()).isPresent
  }
}