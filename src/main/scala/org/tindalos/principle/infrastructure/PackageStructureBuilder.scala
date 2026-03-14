package org.tindalos.principle.infrastructure

import org.tindalos.principle.domain.core.{Package, PackageSorterModule, PackageStructureBuilder}
import org.tindalos.principle.infrastructure.service.jdepend.{JDependPackageAnalyzer, JDependRunner, PackageFactory}
import scala.collection.JavaConverters._

trait PackageListBuilder {
  def build(): List[Package]
}

class JDependBasedPackageListBuilder(rootPackage: String) extends PackageListBuilder {

  private val fn =  {
    val packageListTransformer = {
      val packageFactory = new PackageFactory(rootPackage)
      packageFactory.buildPackageListFactory(packages => PackageSorterModule.sortByName(packages.asJava).asScala.toList)
    }
    JDependPackageAnalyzer.buildAnalyzerFn(
      (rootPkg, filterEnabled) => JDependRunner.preparePackages(rootPkg, filterEnabled).asScala.toList,
      packageListTransformer)
  }
  override def build(): List[Package] = fn.apply(rootPackage)
}

class PackageStructureBuilderImpl() extends PackageStructureBuilder {
  private var cachedBasePackage: Package = _

  override def build(packages: List[Package], rootPackage: String): Package = {

    if (cachedBasePackage == null) {
      val sortedPackages = sortByName(packages, rootPackage)
      val basePackage = sortedPackages.head
      sortedPackages.tail.foreach(aPackage => basePackage.insert(aPackage))
      cachedBasePackage = basePackage
    }
    cachedBasePackage
  }

  private def sortByName(packages:List[Package], basePackageName:String):List[Package] =
    sortByName(packages).filter(_.reference.startsWith(basePackageName))

  private def sortByName(packages:List[Package]): List[Package] = packages.sortBy(_.reference.name)
}
