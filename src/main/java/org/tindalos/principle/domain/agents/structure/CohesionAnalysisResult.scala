package org.tindalos.principle.domain.agents.structure

import org.tindalos.principle.domain.agentscore.AnalysisResult
import org.tindalos.principle.domain.agents.structure.Graph.Node
import org.tindalos.principle.domain.agents.structure.PackageCohesionModule.PackageName
import org.tindalos.principle.domain.agents.structure.PackageStructureHints1Finder.GroupingResult
import org.tindalos.principle.domain.agents.structure.Structure.NodeGroup

case class CohesionAnalysisResult(val packages: Set[(PackageName, NodeGroup)]
                              , val cohesiveNodeGroups: Option[Set[NodeGroup]]
                              , val groupingResult:GroupingResult
                              , val structureHints2:List[(Node,Set[Node],Double)]) extends AnalysisResult {

  override def expectationsFailed(): Boolean = false
}
