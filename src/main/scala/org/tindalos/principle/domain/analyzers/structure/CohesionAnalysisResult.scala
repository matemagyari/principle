package org.tindalos.principle.domain.analyzers.structure

import org.tindalos.principle.domain.AnalysisResult
import org.tindalos.principle.domain.analyzers.structure.PackageCohesionModule.PackageName
import org.tindalos.principle.domain.analyzers.structure.GroupingResult
case class CohesionAnalysisResult(
    packages: Set[(PackageName, NodeGroup)],
    cohesiveNodeGroups: Option[Set[NodeGroup]],
    groupingResult:GroupingResult,
    subgraphDecomposition:SubgraphDecomposition) extends AnalysisResult {

  override def constraintViolated(): Boolean = false
}
