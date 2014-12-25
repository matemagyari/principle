package org.tindalos.principle.domain.detector.adp

import org.tindalos.principle.domain.core.{Cycle, Package, PackageReference}
import org.tindalos.principle.domain.coredetector.{AnalysisInput, Detector}
import org.tindalos.principle.domain.expectations.{Expectations, PackageCoupling}

object CycleDetector {
  def buildInstance(buildPackageStructure: (List[Package], String) => Package) = new Detector {

    override def analyze(input: AnalysisInput) = {

      val basePackage = buildPackageStructure(input.packages,
        input.analysisPlan.basePackage)

      val references = basePackage.toMap()

      var cycles = Map[PackageReference, Set[Cycle]]()

      var sortedByAfferents = references.values.toList.sortBy(_.getMetrics().afferentCoupling)
      if (basePackage.getMetrics().afferentCoupling == 0)
        sortedByAfferents = sortedByAfferents.filterNot(_ equals basePackage)

      while (!sortedByAfferents.isEmpty) {
        val cyclesInSubgraph = sortedByAfferents.head.detectCycles(references)
        cycles = cyclesInSubgraph.mergeBreakingPoints2(cycles)
        sortedByAfferents = sortedByAfferents.filterNot(cyclesInSubgraph.investigatedPackages.contains(_))
      }

      new ADPResult(cycles, input.packageCouplingExpectations().adp)
    }

    override def isWanted(expectations: Expectations) = expectations.packageCoupling match {
      case packageCoupling: PackageCoupling => packageCoupling.adp != null
      case null => false
    }
  }
}