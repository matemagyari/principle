package org.tindalos.principle.domain.core

trait PackageStructureBuilder {

  def build(packages: List[Package], basePackage: String): Package

}

class PackageStructureBuilderImpl() extends PackageStructureBuilder {

  override def build(packages: List[Package], basePackage: String): Package = {
???
  }

}
