package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.core.PackageReference
import org.tindalos.principle.domain.core.ListConverter
import org.tindalos.principle.domain.core.Package
import scala.collection.JavaConversions._

class SubmoduleFactory {

  def buildModules(submoduleDefinitions: SubmoduleDefinitions, packageMap: java.util.Map[PackageReference, Package]): java.util.List[Submodule] = {
    val submodules = submoduleDefinitions.map(convert(ListConverter.convert(packageMap), _))
    ListConverter.convert(submodules)
  }

  private def resolve(packageMap: Map[PackageReference, Package], packageReferences: Set[PackageReference]) = packageReferences.map(getOrThrowException(_, packageMap))

  private def getOrThrowException(reference: PackageReference, packageMap: Map[PackageReference, Package]) =
    packageMap.get(reference) match {
      case None => throw new InvalidBlueprintDefinitionException("Package does not exist: " + reference)
      case Some(aPackage) => aPackage
    }

  private def convert(packageMap: Map[PackageReference, Package], submoduleDefinition: SubmoduleDefinition):Submodule = {
    val packages = resolve(packageMap, submoduleDefinition.packages)
    val submodule = new Submodule(submoduleDefinition.id, ListConverter.convert(packages))
    submodule.defineDependencies(submoduleDefinition.getPlannedDependencies)
    submodule
  }

}