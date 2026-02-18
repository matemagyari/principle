package org.tindalos.principle.domain.core

trait PackageStructureBuilder {

  def build(packages: List[Package], basePackage: String): Package

}


