package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.core.PackageReference
import scala.collection.mutable.Set
import java.util.Collection
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.tindalos.principle.domain.core.ListConverter

class SubmoduleDefinition(val id: SubmoduleId, val packages: scala.collection.immutable.Set[PackageReference]) {

  val legalDependencies = Set[SubmoduleId]()

  def addPlannedDependencies(plannedDependencies: List[SubmoduleId]) = legalDependencies ++= (plannedDependencies)

  def overlapsWith(that: SubmoduleDefinition) = {
    val overlappingPackages = for (aPackage <- packages; otherPackage <- that.packages; if (aPackage.oneContainsAnother(otherPackage))) yield (aPackage, otherPackage)
    !overlappingPackages.isEmpty
  }

  override def equals(other: Any) =
    if (!other.isInstanceOf[SubmoduleDefinition]) false
    else other.asInstanceOf[SubmoduleDefinition].id.eq(id)

  override def hashCode() = new HashCodeBuilder().append(id).hashCode()

  override def toString() = "SubmoduleDefinition [" + id + "]"

  //only for java
  def getPlannedDependencies = ListConverter.convert(legalDependencies)
  def getPackages = ListConverter.convert(packages)
  def addPlannedDependencies(plannedDependencies: java.util.Collection[SubmoduleId]) = legalDependencies ++= ListConverter.convert(plannedDependencies)
  def this(id: SubmoduleId, packages: java.util.Set[PackageReference]) = this(id, ListConverter.convert(packages))

}