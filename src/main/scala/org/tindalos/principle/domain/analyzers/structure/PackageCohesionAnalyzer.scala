package org.tindalos.principle.domain.analyzers.structure

import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.analyzers.Analyzer
import org.tindalos.principle.domain.analyzers.structure.PackageCohesionModule.PackageName
import org.tindalos.principle.domain.analyzers.structure.GroupingResult
import org.tindalos.principle.domain.constraints.Constraints
import scala.collection.JavaConverters._

class PackageCohesionAnalyzer(buildComponents:(PackageName, Set[Node]) => Set[(PackageName, NodeGroup)]
                              , makeStructureHints1: Set[Node] => GroupingResult
                              , findDetachableSubgraphs: Set[Node] => SubgraphDecomposition
                              , collapseToLimit: Set[NodeGroup] => Set[NodeGroup]) extends Analyzer {
    
    override def analyze(input: AnalysisInput) = {

      val packagesWithCohesions = buildComponents(input.analysisPlan.basePackage, input.nodes) 
      val structureHints1 = makeStructureHints1(input.nodes)
      val structureHints2 = findDetachableSubgraphs(input.nodes)

      val cohesiveGroups: Option[Set[NodeGroup]] =
        if (input.packageCouplingExpectations().isPresent) {
          val initialGroups = input.nodes.map(n => new NodeGroup(java.util.Collections.singleton(n)))
          Some(collapseToLimit(initialGroups))
        }
        else None

      val javaPackages = packagesWithCohesions.toMap.asJava
      val javaGroups: java.util.Optional[java.util.Set[NodeGroup]] = cohesiveGroups match {
        case Some(groups) => java.util.Optional.of(groups.asJava)
        case None         => java.util.Optional.empty()
      }

      new CohesionAnalysisResult(javaPackages, javaGroups, structureHints1, structureHints2)
    }

    override def isEnabled(expectations: Constraints) =
      expectations.packageCoupling().isPresent && expectations.packageCoupling().get().grouping().isPresent

}