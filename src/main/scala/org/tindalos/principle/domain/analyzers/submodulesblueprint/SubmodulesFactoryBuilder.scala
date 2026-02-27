package org.tindalos.principle.domain.analyzers.submodulesblueprint

import org.tindalos.principle.domain.core.packages.PackageReference
import org.tindalos.principle.domain.core.{Package, PackageStructureBuilder}
import org.tindalos.principle.infrastructure.analyzers.submodulesblueprint.SubmodulesBlueprintProvider

//class SubmodulesFactory(packageStructureBuilder: PackageStructureBuilder) {
//
//  def buildModules(): Set[Submodule] = {
//
//  }
//}

object SubmodulesFactoryBuilder {

  def buildInstance(packageStructureBuilder: PackageStructureBuilder,
                    submodulesBlueprintProvider: SubmodulesBlueprintProvider,
                    buildSubmodules: (SubmoduleDefinitions, Map[PackageReference, Package]) => Set[Submodule]) =

    (submodulesDefinitionLocation: String, packages: List[Package], basePackageName: String) => {

      val submoduleDefinitions = submodulesBlueprintProvider.readSubmoduleDefinitions(basePackageName, submodulesDefinitionLocation)
      val basePackage = packageStructureBuilder.build(packages, basePackageName)

      buildSubmodules(submoduleDefinitions, basePackage.toMap())
    }


}