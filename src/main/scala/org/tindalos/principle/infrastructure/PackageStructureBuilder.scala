package org.tindalos.principle.infrastructure

import org.tindalos.principle.domain.core.{Package, PackageSorterModule}
import org.tindalos.principle.infrastructure.service.jdepend.{JDependPackageAnalyzer, JDependRunner, PackageFactory}
import scala.collection.JavaConverters._

trait PackageListBuilder {
  def build(): java.util.List[Package]
}

class JDependBasedPackageListBuilder(rootPackage: String) extends PackageListBuilder {

  private val fn: String => List[Package] =  {
    val packageListTransformer = {
      val packageFactory = new PackageFactory(rootPackage)
      packageFactory.buildPackageListFactory(packages => PackageSorterModule.sortByName(packages.asJava).asScala.toList)
    }
    JDependPackageAnalyzer.buildAnalyzerFn(
      (rootPkg, filterEnabled) => JDependRunner.preparePackages(rootPkg, filterEnabled).asScala.toList,
      packageListTransformer)
  }
  override def build(): java.util.List[Package] = fn.apply(rootPackage).asJava
}
