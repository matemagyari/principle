package org.tindalos.principle.domain.agentscore

import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.agents.structure.Graph.Node
import org.tindalos.principle.domain.expectations.{SubmodulesBlueprint, ThirdParty}

case class AnalysisInput(
    packages: List[Package],
    nodes: Set[Node] = Set(),
    analysisPlan: AnalysisPlan) {

  private val expectations = analysisPlan.expectations

  def packageCouplingExpectations() = expectations.packageCoupling

  def layeringExpectations() = expectations.layering

  def thirdPartyExpectations(): Option[ThirdParty] = expectations.thirdParty

  def submodulesBlueprint(): Option[SubmodulesBlueprint] = expectations.submodulesBlueprint

}