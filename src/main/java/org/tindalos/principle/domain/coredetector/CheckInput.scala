package org.tindalos.principle.domain.coredetector

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.core.Package

class CheckInput(val packages: List[Package], val designQualityCheckConfiguration: DesignQualityCheckConfiguration) {

  private val expectations = designQualityCheckConfiguration.expectations

  def basePackage() = designQualityCheckConfiguration.basePackage
  def packageCouplingExpectations() = expectations.packageCoupling
  def layeringExpectations() = expectations.layering
  def thirdPartyExpectations() = expectations.thirdParty
  def submodulesBlueprint() = expectations.submodulesBlueprint


}