package org.tindalos.principle.infrastructure.service.jdepend

import org.tindalos.principle.domain.checker.PackageAnalyzer
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import scala.collection.JavaConversions._
import org.tindalos.principle.domain.core.Package

class JDependPackageAnalyzer(val jDependRunner:JDependRunner,val packageListFactory:PackageListFactory) extends PackageAnalyzer {
  
  def analyze(configuration:DesignQualityCheckConfiguration):List[Package] = {
    val analyzedPackages = jDependRunner.getAnalyzedPackages(configuration.getBasePackage())
    packageListFactory.build(analyzedPackages)
  }

}