package org.tindalos.principle.domain.detector.adp

import org.tindalos.principle.domain.core.Package

object PackageStructureModule {

  def createBuilder(sortByName: (List[Package], String) => List[Package]) = {
    var cachedBasePackage: Package = null
    //synchronized
    (packages: List[Package], basePackageName: String) => {
      if (cachedBasePackage == null) {
        val sortedPackages = sortByName(packages, basePackageName)
        val basePackage = sortedPackages.head
        sortedPackages.tail.foreach(aPackage => basePackage.insert(aPackage))
        cachedBasePackage = basePackage
      }
      cachedBasePackage
    }
  }

}