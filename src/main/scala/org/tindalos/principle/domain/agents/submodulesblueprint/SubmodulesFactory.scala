package org.tindalos.principle.domain.agents.submodulesblueprint

import org.tindalos.principle.domain.core.{Package, PackageReference}

import scala.collection.immutable.Seq

object SubmodulesFactory {

  def buildInstance(packageStructureBuilder: (Seq[Package], String) => Package,
                    readSubmoduleDefinitions: (String, String) => SubmoduleDefinitions,
                    buildSubmodules: (SubmoduleDefinitions, Map[PackageReference, Package]) => Set[Submodule]) =

    (submodulesDefinitionLocation: String, packages: Seq[Package], basePackageName: String) => {

      val submoduleDefinitions = readSubmoduleDefinitions(submodulesDefinitionLocation, basePackageName)
      val basePackage = packageStructureBuilder(packages, basePackageName)

      buildSubmodules(submoduleDefinitions, basePackage.toMap())
    }


}