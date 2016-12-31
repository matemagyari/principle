package org.tindalos.principle.domain.agents.structure

import org.tindalos.principle.domain.agents.structure.Graph.SubgraphDecomposition
import org.tindalos.principle.domain.agents.structure.PackageCohesionModule.PackageName
import org.tindalos.principle.domain.agents.structure.PackageStructureHints1Finder.GroupingResult
import org.tindalos.principle.domain.agents.structure.Structure.NodeGroup
import org.tindalos.principle.domain.agentscore.AnalysisResult

case class CohesionAnalysisResult(
    packages: Set[(PackageName, NodeGroup)],
    cohesiveNodeGroups: Option[Set[NodeGroup]],
    groupingResult:GroupingResult,
    subgraphDecomposition:SubgraphDecomposition) extends AnalysisResult {

  override def expectationsFailed(): Boolean = false
}
