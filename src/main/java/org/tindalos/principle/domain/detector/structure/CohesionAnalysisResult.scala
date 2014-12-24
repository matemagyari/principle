package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.coredetector.AnalysisResult
import org.tindalos.principle.domain.detector.structure.PackageCohesionModule.PackageName
import org.tindalos.principle.domain.detector.structure.Structure.NodeGroup

class CohesionAnalysisResult(val packages: Set[(PackageName, NodeGroup)]) extends AnalysisResult {

  override def expectationsFailed(): Boolean = false
}
