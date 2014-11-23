package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.core.Package

class SubmodulesFactory(
  private val packageStructureBuilder: (List[Package], String) => Package,
  private val submoduleDefinitionsProvider: SubmoduleDefinitionsProvider,
  private val submoduleFactory: SubmoduleFactory) {

  def buildSubmodules(submodulesDefinitionLocation: String, packages: List[Package], basePackageName: String) = {

    val submoduleDefinitions = submoduleDefinitionsProvider.readSubmoduleDefinitions(submodulesDefinitionLocation, basePackageName)
    val basePackage = packageStructureBuilder(packages, basePackageName)

    submoduleFactory.buildModules(submoduleDefinitions, basePackage.toMap())
  }
}