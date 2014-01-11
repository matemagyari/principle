package org.tindalos.principle.domain.expectations

trait DesignQualityExpectations {

  def getLayering(): Layering
  def getPackageCoupling(): PackageCoupling
  def getSubmodulesBlueprint(): SubmodulesBlueprint
}