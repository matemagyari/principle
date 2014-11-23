package org.tindalos.principle.infrastructure.service.jdepend

import jdepend.framework.JavaPackage

import org.tindalos.principle.domain.core.Metrics
import org.tindalos.principle.domain.core.Package

class PackageFactory(metricsCalculator:JavaPackage => Metrics, val basePackage:String) {

  def transform(javaPackage:JavaPackage):Package = {
	  val metrics = metricsCalculator(javaPackage)
	  new LazyLoadingJDependBasedPackage(javaPackage, metrics, this)
  }
  
  def isRelevant(javaPackage:JavaPackage) = javaPackage.getName().startsWith(basePackage)
}