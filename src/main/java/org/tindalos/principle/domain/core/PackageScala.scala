package org.tindalos.principle.domain.core

import scala.collection.mutable.ListBuffer
import org.apache.commons.lang3.builder.HashCodeBuilder
import com.google.common.collect.Lists

abstract class PackageScala(val reference: PackageReference) {
  
  val subPackages:java.util.List[Package] = Lists.newArrayList()

  def this(referenceName: String) = this(new PackageReference(referenceName))

  def isUnreferred(): Boolean
  def getMetrics(): Metrics
  def getOwnPackageReferences(): java.util.Set[PackageReference]

  def instability = getMetrics().instability
  def distance = getMetrics().distance

  def isIsolated() = getMetrics().afferentCoupling == 0 && getMetrics().efferentCoupling == 0;

  override def equals(other: Any) = other.isInstanceOf[PackageScala] && other.asInstanceOf[PackageScala].reference.equals(reference)

  override def hashCode() = new HashCodeBuilder().append(reference).hashCode()

  override def toString() = reference.toString()
}

class TraversedPackages(val packages: List[PackageReference] = List()) {

  def add(reference: PackageReference) = new TraversedPackages(packages :+ reference)
  def from(index: Int) = packages.slice(index, packages.length)
}

object TraversedPackages {
  def empty() = new TraversedPackages()
}