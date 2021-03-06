package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework.JavaPackage
import org.tindalos.principle.domain.core.{Metrics, Package}

import scala.collection.immutable.List

class PackageFactory(rootPackage: String) {

  def isRelevant(javaPackage: JavaPackage) = javaPackage.getName().startsWith(rootPackage)

  def transform(javaPackage: JavaPackage): Package = {
    val metrics = calculateMetrics(javaPackage)
    new LazyLoadingJDependBasedPackage(javaPackage, metrics, this, isRelevant)
  }

  def calculateMetrics(jPackage: JavaPackage) =
    new Metrics(jPackage.afferentCoupling(), jPackage.efferentCoupling(), jPackage.abstractness(), jPackage.instability(), jPackage.distance())


  def buildPackageListFactory(sortByName: List[Package] => List[Package]) =

    (rootPackage:String, analyzedPackages: List[JavaPackage]) => {

      val unsortedList = analyzedPackages
        .filter(isRelevant)
        .map(transform)

      sortByName(unsortedList)
    }
}
