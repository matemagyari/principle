package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.coredetector.{AnalysisInput, Detector}
import org.tindalos.principle.domain.detector.structure.Structure.NodeGroup
import org.tindalos.principle.domain.expectations.{PackageCoupling, Expectations}

object PackageCohesionDetector extends Detector {

  override def analyze(input: AnalysisInput) = {

    val packages = PackageCohesionModule.componentsFromPackages(input.analysisPlan.basePackage, input.nodes)
    val verticalGrouping = PackageStructureFinder.makeGroups(input.nodes)

    val initialGroups = input.nodes.map(n => NodeGroup(Set(n)))
    val cohesiveGroups = CohesiveGroupsDiscoveryModule.collapseToLimit(initialGroups)

    new CohesionAnalysisResult(packages, Option(cohesiveGroups), verticalGrouping)
  }

  override def isWanted(expectations: Expectations) =  expectations.packageCoupling match {
    case packageCoupling: PackageCoupling => packageCoupling.cohesion != null
    case null => false
  }
}