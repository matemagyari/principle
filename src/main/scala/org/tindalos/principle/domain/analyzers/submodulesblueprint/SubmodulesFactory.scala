package org.tindalos.principle.domain.analyzers.submodulesblueprint

import org.tindalos.principle.domain.core.packages.PackageReference
import org.tindalos.principle.domain.core.{Package, PackageStructureBuilder}

object SubmodulesFactory {

  def buildInstance(packageStructureBuilder: PackageStructureBuilder,
                    readSubmoduleDefinitions: (String, String) => SubmoduleDefinitions,
                    buildSubmodules: (SubmoduleDefinitions, Map[PackageReference, Package]) => Set[Submodule]) =

    (submodulesDefinitionLocation: String, packages: List[Package], basePackageName: String) => {

      val submoduleDefinitions = readSubmoduleDefinitions(submodulesDefinitionLocation, basePackageName)
      val basePackage = packageStructureBuilder.build(packages, basePackageName)

      buildSubmodules(submoduleDefinitions, basePackage.toMap())
    }


}