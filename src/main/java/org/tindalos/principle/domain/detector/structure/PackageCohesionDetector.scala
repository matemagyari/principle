package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.coredetector.{AnalysisInput, Detector}
import org.tindalos.principle.domain.detector.structure.PackageCohesionModule.PackageName
import org.tindalos.principle.domain.detector.structure.Structure.NodeGroup
import org.tindalos.principle.domain.expectations.{PackageCoupling, Expectations}

object PackageCohesionDetector extends Detector {

  override def analyze(input: AnalysisInput) = {

    val packages: Set[(PackageName, NodeGroup)] = PackageCohesionModule.componentsFromPackages(input.nodes)

    new CohesionAnalysisResult(packages)
  }

  override def isWanted(expectations: Expectations) =  expectations.packageCoupling match {
    case packageCoupling: PackageCoupling => packageCoupling.cohesion != null
    case null => false
  }
}