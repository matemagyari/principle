package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.core.PackageReference
import org.tindalos.principle.domain.core.Package

object SubmoduleFactory {

  def buildModules(submoduleDefinitions: SubmoduleDefinitions, packageMap: Map[PackageReference, Package]) = {

    def convert(submoduleDefinition: SubmoduleDefinition): Submodule = {
      val packages = submoduleDefinition.packages.map(reference => packageMap.get(reference) match {
        case None => throw new InvalidBlueprintDefinitionException("Package does not exist: " + reference)
        case Some(aPackage) => aPackage
      })
      new Submodule(submoduleDefinition.id, packages, submoduleDefinition.getLegalDependencies)
    }
    submoduleDefinitions.definitions.values.map(convert(_)).toSet
  }



}