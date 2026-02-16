package org.tindalos.principle.domain.agentscore

import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.analyzers.structure.Graph.Node
import org.tindalos.principle.domain.constraints.{SubmodulesBlueprint, ThirdParty, PackageCouplingConstraints}

case class AnalysisInput(
    packages: List[Package],
    nodes: Set[Node] = Set(),
    analysisPlan: AnalysisPlan) {

  private val expectations = analysisPlan.constraints

  private def toScalaOption[T](javaOptional: java.util.Optional[T]): Option[T] = {
    if (javaOptional.isPresent) Some(javaOptional.get()) else None
  }

  def packageCouplingExpectations(): Option[PackageCouplingConstraints] = toScalaOption(expectations.packageCoupling())

  def layeringExpectations() = expectations.layering

  def thirdPartyExpectations(): Option[ThirdParty] = toScalaOption(expectations.thirdParty())

  def submodulesBlueprint(): Option[SubmodulesBlueprint] = toScalaOption(expectations.submodulesBlueprint())

}