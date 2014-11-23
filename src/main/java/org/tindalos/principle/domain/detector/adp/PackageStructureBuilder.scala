package org.tindalos.principle.domain.detector.adp

import org.tindalos.principle.domain.core.{Package, PackageSorter}

object PackageStructureBuilder {

  def build(packageSorter: PackageSorter) = {
    var cachedBasePackage: Package = null
    //synchronized
    (packages: List[Package], basePackageName: String) => {
      if (cachedBasePackage == null) {
        val sortedPackages = packageSorter.sortByName(packages, basePackageName)
        val basePackage = sortedPackages.head
        sortedPackages.tail.foreach(aPackage => basePackage.insert(aPackage))
        cachedBasePackage = basePackage
      }
      cachedBasePackage
    }
  }

}