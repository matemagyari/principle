package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.coredetector.{AnalysisInput, Detector}
import org.tindalos.principle.domain.detector.structure.Structure.NodeGroup
import org.tindalos.principle.domain.expectations.{PackageCoupling, Expectations}

object PackageCohesionDetector extends Detector {

  override def analyze(input: AnalysisInput) = {

    val packagesWithCohesions = PackageCohesionModule.componentsFromPackages(input.analysisPlan.basePackage, input.nodes)
    val structureHints1 = PackageStructureHints1Finder.makeGroups(input.nodes)
    val structureHints2 = Graph.findDetachableSubgraphs(input.nodes)

    val structureHints2WithCohesions = structureHints2.map(x => (x._1,x._2,NodeGroup(x._2).cohesion()))

    val cohesiveGroups = if (input.packageCouplingExpectations().grouping.cohesiveGroupsDiscovery != null) {
      val initialGroups = input.nodes.map(n => NodeGroup(Set(n)))
      Some(CohesiveGroupsDiscoveryModule.collapseToLimit(initialGroups))
    } else None


    new CohesionAnalysisResult(packagesWithCohesions, cohesiveGroups, structureHints1, structureHints2WithCohesions)
  }

  override def isWanted(expectations: Expectations) =  expectations.packageCoupling match {
    case packageCoupling: PackageCoupling => packageCoupling.grouping != null
    case null => false
  }
}