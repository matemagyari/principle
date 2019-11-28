package org.tindalos.principle.domain.agents.adp

import org.tindalos.principle.domain.core.Package

import scala.collection.immutable.Seq

object PackageStructureModule {

  def createBuilder(sortByName: (Seq[Package], String) ⇒ Seq[Package]) = {
    var cachedBasePackage: Package = null
    //synchronized
    (packages: Seq[Package], basePackageName: String) ⇒ {
      if (cachedBasePackage == null) {
        val sortedPackages = sortByName(packages, basePackageName)
        val basePackage = sortedPackages.head
        sortedPackages.tail.foreach(aPackage ⇒ basePackage.insert(aPackage))
        cachedBasePackage = basePackage
      }
      cachedBasePackage
    }
  }

}