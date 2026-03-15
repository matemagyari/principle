package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework.JavaPackage
import org.tindalos.principle.domain.core.packages.{PackageMetrics, PackageReference}
import org.tindalos.principle.domain.core.Package

import scala.collection.JavaConverters._

class LazyLoadingJDependBasedPackage(val javaPackage: JavaPackage, val metrics: PackageMetrics, val packageFactory: PackageFactory,
                                     val isRelevant: JavaPackage => Boolean)
  extends Package(javaPackage.getName()) {

  private val validExternalEfferents = Set("java", "scala")

  override def getMetrics() = metrics

  override def isUnreferred() = metrics.afferentCoupling == 0

  override def getOwnPackageReferences(): java.util.Set[PackageReference] = {

    javaPackage.getEfferents().asScala
      .filter(isRelevant(_))
      .map(packageFactory.transform(_).reference)
      .toSet
      .asJava
  }

  override def getOwnExternalPackageReferences(): java.util.Set[PackageReference] =

    javaPackage.getEfferents().asScala
      .filter(p => !isRelevant(p) && isNotValidExternalEfferent(p))
      .map(packageFactory.transform(_).reference)
      .toSet
      .asJava


  private def isNotValidExternalEfferent(p: JavaPackage) = !validExternalEfferents.exists(e => p.getName().startsWith(e))


}