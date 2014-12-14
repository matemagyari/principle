package org.tindalos.principle.infrastructure.service.jdepend

import java.util.Collection

import jdepend.framework.JavaPackage
import org.tindalos.principle.domain.core.{Metrics, Package, PackageReference}

import scala.collection.JavaConversions._

class LazyLoadingJDependBasedPackage(val javaPackage: JavaPackage, val metrics: Metrics, val packageFactory: PackageFactory,
                                      val isRelevant: JavaPackage => Boolean)
  extends Package(javaPackage.getName()) {

  private val validExternalEfferents = Set("java", "scala")

  override def getMetrics() = metrics

  override def isUnreferred() = metrics.afferentCoupling == 0

  override def getOwnPackageReferences(): Set[PackageReference] = {

    javaPackage.getEfferents().asInstanceOf[Collection[JavaPackage]]
      .filter(isRelevant(_))
      .map(packageFactory.transform(_).reference)
      .toSet
  }

  override def getOwnExternalPackageReferences() =

    javaPackage.getEfferents().asInstanceOf[Collection[JavaPackage]]
      .filter(p => !isRelevant(p) && isNotValidExternalEfferent(p))
      .map(packageFactory.transform(_).reference)
      .toSet


  private def isNotValidExternalEfferent(p: JavaPackage) = !validExternalEfferents.exists(e => p.getName().startsWith(e))


}