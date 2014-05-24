package org.tindalos.principle.infrastructure.service.jdepend

import java.util.Collection

import org.tindalos.principle.domain.core.Metrics
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.PackageReference

import scala.collection.JavaConversions._

import jdepend.framework.JavaPackage

class JDependBasedPackage(val javaPackage: JavaPackage, val basePackage: String, val metrics: Metrics)
  extends Package(javaPackage.getName()) {

  override def getMetrics() = metrics
  override def isUnreferred() = metrics.afferentCoupling == 0
  override def getOwnPackageReferences() = ownPackageReferences

  private val ownPackageReferences = 
    javaPackage.getEfferents().asInstanceOf[Collection[JavaPackage]]
      .filter(_.getName().startsWith(basePackage))
      .map(x => new PackageReference(x.getName()))
      .toSet

}