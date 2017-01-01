package org.tindalos.principle.domain.expectations


case class Checks(
    override val layering: Layering = null,
    override val thirdParty: Option[ThirdParty] = None,
    override val packageCoupling: PackageCoupling = null,
    override val submodulesBlueprint: Option[SubmodulesBlueprint] = None) extends Expectations

trait Expectations {

  def layering: Layering
  def packageCoupling: PackageCoupling
  def submodulesBlueprint: Option[SubmodulesBlueprint]
  def thirdParty: Option[ThirdParty]
}