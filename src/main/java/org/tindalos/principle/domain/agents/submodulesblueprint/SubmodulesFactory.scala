package org.tindalos.principle.domain.agents.submodulesblueprint

import org.tindalos.principle.domain.core.{Package, PackageReference}

object SubmodulesFactory {

  def buildInstance(packageStructureBuilder: (List[Package], String) => Package,
                    readSubmoduleDefinitions: (String, String) => SubmoduleDefinitions,
                    buildSubmodules: (SubmoduleDefinitions, Map[PackageReference, Package]) => Set[Submodule]) =

    (submodulesDefinitionLocation: String, packages: List[Package], basePackageName: String) => {

      val submoduleDefinitions = readSubmoduleDefinitions(submodulesDefinitionLocation, basePackageName)
      val basePackage = packageStructureBuilder(packages, basePackageName)

      buildSubmodules(submoduleDefinitions, basePackage.toMap())
    }


}