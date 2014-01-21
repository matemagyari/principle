package org.tindalos.principle.domain.detector.submodulesblueprint

trait SubmoduleDefinitionsProvider {
  
  def readSubmoduleDefinitions(submodulesDefinitionLocation:String, basePackageName:String):SubmoduleDefinitions

}