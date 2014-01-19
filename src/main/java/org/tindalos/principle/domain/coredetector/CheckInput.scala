package org.tindalos.principle.domain.coredetector

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.core.Package

class CheckInput(val packages: List[Package], val designQualityCheckConfiguration: DesignQualityCheckConfiguration) {

  def getBasePackage() = designQualityCheckConfiguration.basePackage
  def getPackageCouplingExpectations() = getExpectations.getPackageCoupling()
  def getLayeringExpectations() = getExpectations.getLayering()
  def getSubmodulesBlueprint() = getExpectations.getSubmodulesBlueprint()
  
  private def getExpectations = designQualityCheckConfiguration.expectations
  

}