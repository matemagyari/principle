package org.tindalos.principle.domain.coredetector

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.core.Package

class CheckInput(val packages: List[Package], val designQualityCheckConfiguration: DesignQualityCheckConfiguration) {

  def getBasePackage() = designQualityCheckConfiguration.basePackage
  def getPackageCouplingExpectations() = expectations.packageCoupling
  def getLayeringExpectations() = expectations.layering
  def getSubmodulesBlueprint() = expectations.submodulesBlueprint
  
  private val expectations = designQualityCheckConfiguration.expectations
  

}