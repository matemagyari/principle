package org.tindalos.principle.infrastructure

import org.tindalos.principle.domain.core.{Package, PackageSorterModule}
import org.tindalos.principle.infrastructure.service.jdepend.{JDependPackageAnalyzer, JDependRunner, PackageFactory}

trait PackageStructureBuilder {
  def build(packages: List[Package], rootPackage: String): List[Package]
}

trait PackageListBuilder {
  def build(): List[Package]
}

class JDependBasedPackageListBuilder(rootPackage: String) extends PackageListBuilder {

  private val fn =  {
    val packageListTransformer = {
      val packageFactory = new PackageFactory(rootPackage)
      packageFactory.buildPackageListFactory(PackageSorterModule.sortByName(_))
    }
    JDependPackageAnalyzer.buildAnalyzerFn(JDependRunner.preparePackages, packageListTransformer)
  }
  override def build(): List[Package] = fn.apply(rootPackage)
}

object PackageStructureBuilderImpl extends PackageStructureBuilder {
  override def build(packages: List[Package], rootPackage: String): List[Package] = {
???
  }
}
