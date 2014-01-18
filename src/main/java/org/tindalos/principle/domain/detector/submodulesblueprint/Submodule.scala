package org.tindalos.principle.domain.detector.submodulesblueprint

import org.tindalos.principle.domain.core.Package
import org.tindalos.principle.domain.core.ListConverter
import org.tindalos.principle.domain.core.PackageReference
import scala.collection.immutable.Set
import org.apache.commons.lang3.builder.HashCodeBuilder

class Submodule(val id: SubmoduleId, val packagesUnderModule: Set[Package], val plannedDependencies: Set[SubmoduleId]) {

  //TODO move this check to the definition
  if (plannedDependencies.contains(id)) throw new InvalidBlueprintDefinitionException("Submodule should not depend on itself: " + id)

  val outgoingReferences = packagesUnderModule.flatMap(x => ListConverter.convert(x.accumulatedDirectPackageReferences())).toSet

  def findMissingPredefinedDependencies(otherSubmodules: Set[Submodule]) = {

    assert(!otherSubmodules.contains(this))

    otherSubmodules
      .filter(x => plannedDependencies.contains(x.id))
      .filter(!_.isReferredBy(outgoingReferences))
      .toSet
  }

  def findIllegalDependencies(otherSubmodules: Set[Submodule]):Set[Submodule] = {

    assert(!otherSubmodules.contains(this))

    val illegalDependencies = otherSubmodules.filter(x => !plannedDependencies.contains(x.id)).toSet
    val legalDependencies = otherSubmodules.filter(x => plannedDependencies.contains(x.id)).toSet

    if (illegalDependencies.isEmpty) Set()
    else {
      val extraReferences = calculateExtraReferences(legalDependencies)
      otherSubmodules.filter(_.isReferredBy(extraReferences))
    }
  }

  private def isReferredBy(references: Set[PackageReference]) = {

    val results = for (
      packageReference <- references;
      aPackage <- packagesUnderModule;
      if (packageReference.pointsToThatOrInside(aPackage.getReference()))
    ) yield 1

    !results.isEmpty
  }

  private def calculateExtraReferences(legalDependencies: Set[Submodule]) = {
    var potentiallyIllegalReferences = outgoingReferences
    legalDependencies.foreach({ legalDependency =>
      potentiallyIllegalReferences = legalDependency.removeOutsideReferences(potentiallyIllegalReferences)
    })
    potentiallyIllegalReferences
  }

  private def removeOutsideReferences(references: Set[PackageReference]) =
    references.filterNot(ref => packagesUnderModule.exists(pac => ref.pointsToThatOrInside(pac.getReference())))

  override def equals(other: Any) =
    if (!other.isInstanceOf[Submodule]) false
    else other.asInstanceOf[Submodule].id equals id

  override def hashCode = new HashCodeBuilder().append(id).hashCode()

  override def toString() = id.toString()

}