package org.tindalos.principle.infrastructure.service.jdepend

import java.util.Collection

import jdepend.framework.JavaPackage
import org.tindalos.principle.domain.core.{Metrics, Package, PackageReference}

import scala.collection.JavaConversions._

class JDependBasedPackage(val javaPackage: JavaPackage, val basePackage: String, val metrics: Metrics)
  extends Package(javaPackage.getName()) {

  private val validExternalEfferents = Set("java", "scala")

  override def getMetrics() = metrics

  override def isUnreferred() = metrics.afferentCoupling == 0

  override def getOwnPackageReferences() = ownPackageReferences

  override def getOwnExternalPackageReferences(): Set[PackageReference] = ownExternalPackageReferences

  private lazy val ownPackageReferences =
    javaPackage.getEfferents().asInstanceOf[Collection[JavaPackage]]
      .filter(_.getName().startsWith(basePackage))
      .map(x ⇒ new PackageReference(x.getName()))
      .toSet

  private lazy val ownExternalPackageReferences =

    javaPackage.getEfferents().asInstanceOf[Collection[JavaPackage]]
      .filter(p ⇒ !p.getName().startsWith(basePackage) && isNotValidExternalEfferent(p))
      .map(x ⇒ new PackageReference(x.getName()))
      .toSet

  def isNotValidExternalEfferent(p: JavaPackage) = !validExternalEfferents.exists(e ⇒ p.getName().startsWith(e))

}