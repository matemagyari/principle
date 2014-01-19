package org.tindalos.principle.domain.expectations

trait DesignQualityExpectations {

  def layering: Layering
  def packageCoupling: PackageCoupling
  def submodulesBlueprint: SubmodulesBlueprint
}