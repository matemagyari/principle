package org.tindalos.principle.domain.analyzers.submodulesblueprint

import org.tindalos.principle.domain.core.PackageReference
import org.tindalos.principle.domain.core.Package

object SubmoduleFactory {

  def buildModules(submoduleDefinitions: SubmoduleDefinitions, packageMap: Map[PackageReference, Package]) = {
    import scala.collection.JavaConverters._

    def convert(submoduleDefinition: SubmoduleDefinition): Submodule = {
      val packages = submoduleDefinition.packages().asScala.map(reference => packageMap.get(reference) match {
        case None => throw new InvalidBlueprintDefinitionException("Package does not exist: " + reference)
        case Some(aPackage) => aPackage
      })
      new Submodule(submoduleDefinition.id, packages.toSet, submoduleDefinition.getLegalDependencies.asScala.toSet)
    }
    submoduleDefinitions.definitions.values.map(convert).toSet
  }
}