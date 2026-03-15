package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework.JavaPackage
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.packages.PackageMetrics

import scala.collection.immutable.List

class PackageFactory(rootPackage: String) {

  def isRelevant(javaPackage: JavaPackage): Boolean = javaPackage.getName().startsWith(rootPackage)

  def transform(javaPackage: JavaPackage): Package = {
    val metrics = calculateMetrics(javaPackage)
    new LazyLoadingJDependBasedPackage(javaPackage, metrics, this, isRelevant)
  }

  private def calculateMetrics(jPackage: JavaPackage) =
    new PackageMetrics(jPackage.afferentCoupling(), jPackage.efferentCoupling(), jPackage.abstractness(), jPackage.instability(), jPackage.distance())


  def buildPackageListFactory(sortByName: List[Package] => List[Package]):List[JavaPackage] => List[Package] =

    analyzedPackages => {

      val unsortedList = analyzedPackages
        .filter(isRelevant)
        .map(transform)

      sortByName(unsortedList)
    }
}
