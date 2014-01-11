package org.tindalos.principle.infrastructure.service.jdepend

import org.tindalos.principle.domain.checker.PackageAnalyzer
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import scala.collection.JavaConversions._
import org.tindalos.principle.domain.core.ListConverter
import org.tindalos.principle.domain.core.Package

class JDependPackageAnalyzer(val jDependRunner:JDependRunner,val packageListFactory:PackageListFactory) extends PackageAnalyzer {
  
  def analyze(configuration:DesignQualityCheckConfiguration) = {
    val analyzedPackages = jDependRunner.getAnalyzedPackages(configuration.getBasePackage())
    val javaList:java.util.List[Package] = packageListFactory.build(analyzedPackages)
    ListConverter.convert(javaList)
  }

}