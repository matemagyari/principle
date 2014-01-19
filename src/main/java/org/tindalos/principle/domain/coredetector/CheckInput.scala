package org.tindalos.principle.domain.coredetector

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.core.Package

class CheckInput(val packages: List[Package], val designQualityCheckConfiguration: DesignQualityCheckConfiguration) {

  def getBasePackage() = designQualityCheckConfiguration.basePackage
  def getPackageCouplingExpectations() = getExpectations.packageCoupling
  def getLayeringExpectations() = getExpectations.layering
  def getSubmodulesBlueprint() = getExpectations.submodulesBlueprint
  
  private def getExpectations = designQualityCheckConfiguration.expectations
  

}