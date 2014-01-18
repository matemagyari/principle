package org.tindalos.principle.domain.core

import scala.collection.mutable.ListBuffer

abstract class PackageScala {

}

class TraversedPackages(val packages: List[PackageReference] = List()) {

  def add(reference: PackageReference) = new TraversedPackages(packages :+ reference)
  def from(index: Int) = packages.slice(index, packages.length)
}

object TraversedPackages {
  def empty() = new TraversedPackages()
}