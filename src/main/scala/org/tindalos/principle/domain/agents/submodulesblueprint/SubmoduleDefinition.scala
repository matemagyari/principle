package org.tindalos.principle.domain.agents.submodulesblueprint

import org.tindalos.principle.domain.core.PackageReference
import scala.collection.mutable.Set
import org.apache.commons.lang3.builder.HashCodeBuilder

import scala.collection.immutable.Seq

class SubmoduleDefinition(val id: SubmoduleId, val packages: scala.collection.immutable.Set[PackageReference]) {

  private val legalDependencies = Set[SubmoduleId]()

  def addPlannedDependencies(plannedDependencies: Seq[SubmoduleId]) = legalDependencies ++= plannedDependencies

  def overlapsWith(that: SubmoduleDefinition) = {
    val overlappingPackages = for (aPackage <- packages; otherPackage <- that.packages; if aPackage.oneContainsAnother(otherPackage))
    yield (aPackage, otherPackage)
    !overlappingPackages.isEmpty
  }

  def getLegalDependencies = legalDependencies.toSet

  override def equals(other: Any) =
    if (!other.isInstanceOf[SubmoduleDefinition]) false
    else other.asInstanceOf[SubmoduleDefinition].id.eq(id)

  override def hashCode() = new HashCodeBuilder().append(id).hashCode()

  override def toString() = "SubmoduleDefinition [" + id + "]"

}