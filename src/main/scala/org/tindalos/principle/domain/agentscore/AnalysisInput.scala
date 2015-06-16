package org.tindalos.principle.domain.agentscore

import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.agents.structure.Graph.Node

class AnalysisInput(val packages: List[Package], val nodes:Set[Node] = Set(), val analysisPlan: AnalysisPlan) {

  private val expectations = analysisPlan.expectations

  def packageCouplingExpectations() = expectations.packageCoupling
  def layeringExpectations() = expectations.layering
  def thirdPartyExpectations() = expectations.thirdParty
  def submodulesBlueprint() = expectations.submodulesBlueprint


}