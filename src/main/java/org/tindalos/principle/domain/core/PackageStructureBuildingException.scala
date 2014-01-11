package org.tindalos.principle.domain.core

case class PackageStructureBuildingException(val msg: String)
  extends RuntimeException(msg) 