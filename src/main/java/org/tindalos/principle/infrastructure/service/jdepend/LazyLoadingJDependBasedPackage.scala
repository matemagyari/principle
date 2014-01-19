package org.tindalos.principle.infrastructure.service.jdepend

import org.tindalos.principle.domain.core.Metrics
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.PackageReference
import jdepend.framework.JavaPackage
import java.util.Collection
import scala.collection.JavaConversions._

class LazyLoadingJDependBasedPackage(val javaPackage: JavaPackage, val metrics: Metrics, val packageFactory: PackageFactory)
  extends Package(javaPackage.getName()) {

  override def getMetrics() = metrics
  override def isUnreferred = metrics.afferentCoupling == 0

  override def getOwnPackageReferences(): Set[PackageReference] = {

    javaPackage.getEfferents().asInstanceOf[Collection[JavaPackage]]
    					.filter(packageFactory.isRelevant(_))
    					.map(packageFactory.transform(_).reference)
    					.toSet
  }
}