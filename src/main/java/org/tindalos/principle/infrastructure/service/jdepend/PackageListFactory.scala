package org.tindalos.principle.infrastructure.service.jdepend

import org.tindalos.principle.domain.core.PackageSorter
import jdepend.framework.JavaPackage
import scala.collection.immutable.List
import org.tindalos.principle.domain.core.Package

object PackageListFactory {

  def buildPackageListFactory(packageFactory:PackageFactory, packageSorter:PackageSorter) =

    (analyzedPackages: List[JavaPackage]) => {

      val unsortedList = analyzedPackages
        .filter(packageFactory.isRelevant(_))
        .map(packageFactory.transform(_))

      packageSorter.sortByName(unsortedList)
    }
}