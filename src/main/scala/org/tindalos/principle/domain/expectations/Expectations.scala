package org.tindalos.principle.domain.expectations

trait Expectations {

  def layering: Layering
  def packageCoupling: PackageCoupling
  def submodulesBlueprint: SubmodulesBlueprint
  def thirdParty: Option[ThirdParty]
}