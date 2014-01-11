package org.tindalos.principle.domain.detector.adp

import org.tindalos.principle.domain.core.PackageSorter
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.ListConverter

class PackageStructureBuilder(val packageSorter:PackageSorter) {
  
  private var cachedBasePackage:Package = null
  
  //synchronized
   def build(packages:List[Package], basePackageName:String):Package = {
        if (cachedBasePackage == null) {
            val sortedPackages = packageSorter.sortByName(packages, basePackageName)
            val basePackage = sortedPackages.head
            sortedPackages.tail.foreach( aPackage => basePackage.insert(aPackage))
            cachedBasePackage = basePackage
        }
        cachedBasePackage
    }
  
  def build(packages:java.util.List[Package], basePackageName:String):Package = {
    build(ListConverter.convert(packages), basePackageName)
  }

}