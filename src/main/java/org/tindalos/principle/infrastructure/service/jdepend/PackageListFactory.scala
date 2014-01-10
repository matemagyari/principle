package org.tindalos.principle.infrastructure.service.jdepend

import org.tindalos.principle.domain.core.PackageSorter
import java.util.Collection
import jdepend.framework.JavaPackage
import scala.collection.immutable.List
import org.tindalos.principle.domain.core.Package
import java.util.ArrayList

//eliminate java.util.List
class PackageListFactory(val packageFactory:PackageFactory, val packageSorter:PackageSorter) {

  def build(analyzedPackagesJava: Collection[JavaPackage]) = {
    
    val analyzedPackages:List[JavaPackage] = List.fromArray(analyzedPackagesJava.toArray()).asInstanceOf[List[JavaPackage]]
    
    val unsortedList:List[Package] = analyzedPackages
    					.filter(packageFactory.isRelevant(_))
    					.map(packageFactory.transform(_))
    val unsortedListJava = new ArrayList[Package]()
    
    for (element <- unsortedList) {
      unsortedListJava.add(element);
    }
    packageSorter.sortByName(unsortedListJava)					
  }
}