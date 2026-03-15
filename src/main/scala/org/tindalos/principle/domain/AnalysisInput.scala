package org.tindalos.principle.domain

import org.tindalos.principle.domain.analyzers.structure.Node
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmoduleDefinitions
import org.tindalos.principle.domain.constraints.{Layering, PackageCouplingConstraints, ThirdParty}
import org.tindalos.principle.domain.core.packages.PackageWithMetrics
import org.tindalos.principle.domain.core.AnalysisPlan

case class AnalysisInput(
                          packages: List[PackageWithMetrics],
                          nodes: Set[Node] = Set(),
                          analysisPlan: AnalysisPlan) {

  private val expectations = analysisPlan.constraints

  private def toScalaOption[T](javaOptional: java.util.Optional[T]): Option[T] = {
    if (javaOptional.isPresent) Some(javaOptional.get()) else None
  }

  def packageCouplingExpectations(): java.util.Optional[PackageCouplingConstraints] = expectations.packageCoupling()

  def layeringExpectations(): Option[Layering] = toScalaOption(expectations.layering())

  def thirdPartyExpectations(): Option[ThirdParty] = toScalaOption(expectations.thirdParty())

  def submoduleDefinitions(): Option[SubmoduleDefinitions] = toScalaOption(expectations.submoduleDefinitions())

}