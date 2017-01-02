package org.tindalos.principle.domain.agents.structure

import org.tindalos.principle.domain.agentscore.{AnalysisResult, AnalysisInput, Agent}
import org.tindalos.principle.domain.agents.structure.Graph.{SubgraphDecomposition, Peninsula, Node}
import org.tindalos.principle.domain.agents.structure.PackageCohesionModule.PackageName
import org.tindalos.principle.domain.agents.structure.PackageStructureHints1Finder.GroupingResult
import org.tindalos.principle.domain.agents.structure.Structure.NodeGroup
import org.tindalos.principle.domain.expectations.{PackageCoupling, Checks}

object PackageCohesionDetector {
  
  def buildAgent(buildComponents:(PackageName, Set[Node]) => Set[(PackageName, NodeGroup)]
             , makeStructureHints1: Set[Node] => GroupingResult
             , findDetachableSubgraphs: Set[Node] => SubgraphDecomposition
             , collapseToLimit: Set[NodeGroup] => Set[NodeGroup]) = new Agent {
    
    override def analyze(input: AnalysisInput) = {

      val packagesWithCohesions = buildComponents(input.analysisPlan.basePackage, input.nodes) 
      val structureHints1 = makeStructureHints1(input.nodes)
      val structureHints2 = findDetachableSubgraphs(input.nodes)

      val cohesiveGroups =
        input.packageCouplingExpectations().map { _ ⇒
          val initialGroups = input.nodes.map(n => NodeGroup(Set(n)))
          collapseToLimit(initialGroups)
        }

      CohesionAnalysisResult(packagesWithCohesions, cohesiveGroups, structureHints1, structureHints2)
    }

    override def isWanted(expectations: Checks) =  expectations.packageCoupling
        .map(_.grouping != null)
        .getOrElse(false)
  }


}