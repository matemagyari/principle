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

  def packageCouplingExpectations(): java.util.Optional[PackageCouplingConstraints] = expectations.packageCoupling()

  def layeringExpectations(): java.util.Optional[Layering] = expectations.layering()

  def thirdPartyExpectations(): java.util.Optional[ThirdParty] = expectations.thirdParty()

  def submoduleDefinitions(): java.util.Optional[SubmoduleDefinitions] = expectations.submoduleDefinitions()

}