package org.tindalos.principle.domain.coredetector

import org.tindalos.principle.domain.core.ExpectationsConfig
import org.tindalos.principle.domain.core.Package

class PackagesAndExpectations(val packages: List[Package], val expectationsConfig: ExpectationsConfig) {

  private val expectations = expectationsConfig.expectations

  def packageCouplingExpectations() = expectations.packageCoupling
  def layeringExpectations() = expectations.layering
  def thirdPartyExpectations() = expectations.thirdParty
  def submodulesBlueprint() = expectations.submodulesBlueprint


}