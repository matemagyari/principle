package org.tindalos.principle.domain.detector.adp

import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.tindalos.principle.domain.expectations.PackageCoupling
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.core.CyclesInSubgraph
import org.tindalos.principle.domain.core.Cycle
import com.google.common.collect.Maps
import org.tindalos.principle.domain.core.PackageReference

class CycleDetector(private val packageStructureBuilder: PackageStructureBuilder) extends Detector {

  override def analyze(checkInput: CheckInput) = {

    val basePackage = packageStructureBuilder.build(checkInput.packages,
      checkInput.designQualityCheckConfiguration.basePackage)

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
    new ADPResult(cycles, checkInput.getPackageCouplingExpectations().adp)
  }

  override def isWanted(expectations: DesignQualityExpectations) = expectations.packageCoupling match {
    case packageCoupling: PackageCoupling => packageCoupling.adp != null
    case null => false
  }

}