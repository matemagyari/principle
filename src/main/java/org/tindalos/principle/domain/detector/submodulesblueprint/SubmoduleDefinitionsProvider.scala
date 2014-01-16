package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.expectations.SubmodulesDefinitionLocation

trait SubmoduleDefinitionsProvider {
  
  def readSubmoduleDefinitions(submodulesDefinitionLocation:SubmodulesDefinitionLocation, basePackageName:String):SubmoduleDefinitions

}