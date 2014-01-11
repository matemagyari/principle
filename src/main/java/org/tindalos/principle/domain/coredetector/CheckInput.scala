package org.tindalos.principle.domain.coredetector

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.ListConverter

class CheckInput(val packages: List[Package], val designQualityCheckConfiguration: DesignQualityCheckConfiguration) {

  def getBasePackage() = designQualityCheckConfiguration.getBasePackage()
  def getPackageCouplingExpectations() = getExpectations.getPackageCoupling()
  def getLayeringExpectations() = getExpectations.getLayering()
  def getSubmodulesBlueprint() = getExpectations.getSubmodulesBlueprint()
  
  private def getExpectations = designQualityCheckConfiguration.getExpectations()
  
  def getPackages():java.util.List[Package] = ListConverter.convert(packages)

}