package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.detector.structure.PackageCohesionModule.PackageName
import org.tindalos.principle.domain.detector.structure.PackageStructureFinder.GroupingResult
import org.tindalos.principle.domain.detector.structure.Structure.NodeGroup

case class CohesionAnalysisResult(val packages: Set[(PackageName, NodeGroup)]
                              , val cohesiveNodeGroups: Option[Set[NodeGroup]]
                              , val groupingResult:GroupingResult) extends AnalysisResult {

  override def expectationsFailed(): Boolean = false
}
