package org.tindalos.principle.infrastructure

trait PackageStructureBuilder {
  def build(packages: List[Package], rootPackage: String): List[Package]
}

object PackageStructureBuilderImpl extends PackageStructureBuilder {
  override def build(packages: List[Package], rootPackage: String): List[Package] = {
???
  }
}
