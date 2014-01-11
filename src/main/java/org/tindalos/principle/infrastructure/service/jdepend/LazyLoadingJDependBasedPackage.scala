package org.tindalos.principle.infrastructure.service.jdepend

import org.tindalos.principle.domain.core.Metrics
import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.PackageReference
import jdepend.framework.JavaPackage
import org.tindalos.principle.domain.core.ListConverter
import java.util.Collection
import java.util.Set

class LazyLoadingJDependBasedPackage(val javaPackage: JavaPackage, val metrics: Metrics, val packageFactory: PackageFactory)
  extends Package(javaPackage.getName()) {

  override def getMetrics() = metrics
  override def isUnreferred = metrics.afferentCoupling == 0

  override def getOwnPackageReferences(): java.util.Set[PackageReference] = {

    val efferents: List[JavaPackage] = ListConverter.convert(javaPackage.getEfferents().asInstanceOf[Collection[JavaPackage]])
    val references = efferents
    					.filter(packageFactory.isRelevant(_))
    					.map(packageFactory.transform(_).getReference())
    					
    ListConverter.convert(references.toSet)
  }
}