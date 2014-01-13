package org.tindalos.principle.domain.detector.adp

import org.tindalos.principle.domain.coredetector.Detector
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.tindalos.principle.domain.expectations.PackageCoupling
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.coredetector.CheckInput
import org.tindalos.principle.domain.core.CyclesInSubgraph
import org.tindalos.principle.domain.core.ListConverter
import org.tindalos.principle.domain.core.Cycle
import com.google.common.collect.Maps
import org.tindalos.principle.domain.core.PackageReference

class CycleDetector(private val packageStructureBuilder: PackageStructureBuilder) extends Detector {

  override def analyze(checkInput: CheckInput) = {

    val basePackage = packageStructureBuilder.build(checkInput.getPackages(),
      checkInput.designQualityCheckConfiguration.basePackage)
      
    val references = basePackage.toMap()

    var cycles = Map[PackageReference, Set[Cycle]]()

    var sortedByAfferents = ListConverter.convert(references.values()).sortBy(_.getMetrics().afferentCoupling)
    if (basePackage.getMetrics().afferentCoupling == 0) {
      sortedByAfferents = sortedByAfferents.filterNot(_ equals basePackage)
    }
    while (!sortedByAfferents.isEmpty) {
      val cyclesInSubgraph = sortedByAfferents.head.detectCycles(references)
      cycles = merge(cycles, cyclesInSubgraph.breakingPoints)
      sortedByAfferents = sortedByAfferents.filterNot(cyclesInSubgraph.investigatedPackages_.contains(_))
    }
    new ADPResult(cycles, checkInput.getPackageCouplingExpectations().getADP())
  }
  override def isWanted(expectations: DesignQualityExpectations) = expectations.getPackageCoupling match {
    case packageCoupling: PackageCoupling => packageCoupling.getADP() != null
    case null => false
  }

  private def merge[T](a: Map[PackageReference, scala.collection.immutable.Set[T]],
    b: scala.collection.mutable.Map[PackageReference, scala.collection.mutable.Set[T]]) = {
    var merged = scala.collection.immutable.Map[PackageReference, Set[T]]()
    for ((ka, va) <- a) {
      merged = merged + (ka -> va)
    }
    for ((kb, vb) <- b) {
      merged = merged + (kb -> vb.toSet)
    }
    merged.toMap
  }

}