package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.detector.adp.PackageStructureBuilder
import org.tindalos.principle.domain.expectations.SubmodulesDefinitionLocation
import org.tindalos.principle.domain.core.Package

class SubmodulesFactory(
  private val packageStructureBuilder: PackageStructureBuilder,
  private val submoduleDefinitionsProvider: SubmoduleDefinitionsProvider,
  private val submoduleFactory: SubmoduleFactory) {

  def buildSubmodules(submodulesDefinitionLocation: SubmodulesDefinitionLocation, packages: List[Package], basePackageName: String) = {

    val submoduleDefinitions = submoduleDefinitionsProvider.readSubmoduleDefinitions(submodulesDefinitionLocation, basePackageName)
    val basePackage = packageStructureBuilder.build(packages, basePackageName)

    submoduleFactory.buildModules(submoduleDefinitions, basePackage.toMap())
  }
}