package org.tindalos.principle.infrastructure.service.jdepend

import org.tindalos.principle.domain.core.PackageSorter
import jdepend.framework.JavaPackage
import scala.collection.immutable.List
import org.tindalos.principle.domain.core.Package

class PackageListFactory(private val packageFactory:PackageFactory, private val packageSorter:PackageSorter) {

  def build(analyzedPackages: List[JavaPackage]) = {
    
    val unsortedList = analyzedPackages
    					.filter(packageFactory.isRelevant(_))
    					.map(packageFactory.transform(_))
    packageSorter.sortByName(unsortedList)					
  }
}