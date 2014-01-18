package org.tindalos.principle.domain.core

import scala.collection.mutable.ListBuffer
import org.apache.commons.lang3.builder.HashCodeBuilder
import com.google.common.collect.Lists
import scala.collection.JavaConversions._
import com.google.common.collect.Sets

abstract class PackageScala(val reference: PackageReference) {

  val subPackages: java.util.List[Package] = Lists.newArrayList()

  def this(referenceName: String) = this(new PackageReference(referenceName))

  def isUnreferred(): Boolean
  def getMetrics(): Metrics
  def getOwnPackageReferences(): java.util.Set[PackageReference]

  def instability = getMetrics().instability
  def distance = getMetrics().distance

  def isIsolated() = getMetrics().afferentCoupling == 0 && getMetrics().efferentCoupling == 0

  def insert(aPackage: Package) {
    if (this.equals(aPackage)) {
      throw new PackageStructureBuildingException("Attempted to insert into itself " + this)
    } else if (this.doesNotContain(aPackage)) {
      throw new PackageStructureBuildingException("Attempted to insert " + aPackage + " into " + this)
    } else if (this.isDirectSuperPackageOf(aPackage)) {
      subPackages.add(aPackage)
    } else {
      insertIndirectSubPackage(aPackage)
    }
  }

  // all the references going out from this package
  def accumulatedDirectPackageReferences(): java.util.Set[PackageReference] =
    subPackages.flatMap(_.accumulatedDirectPackageReferences()).toSet.filterNot(_.equals(reference)).toSet ++: getOwnPackageReferences()

  protected def accumulatedDirectlyReferredPackages(packageReferenceMap: java.util.Map[PackageReference, Package]):java.util.Set[Package] =
    accumulatedDirectPackageReferences().map(packageReferenceMap.get(_))

  def toMap(): java.util.Map[PackageReference, Package] = toMap(new java.util.HashMap[PackageReference, Package]())

  protected def toMap(accumulatingMap: java.util.Map[PackageReference, Package]): java.util.Map[PackageReference, Package] = {

    accumulatingMap.put(reference, this.asInstanceOf[Package])
    subPackages.foreach(child => child.asInstanceOf[PackageScala].toMap(accumulatingMap))
    accumulatingMap
  }

  protected def createNew(name: String) = {
    new Package(name) {
      override def getOwnPackageReferences() = Sets.newHashSet()
      override def getMetrics() = UndefinedMetrics
      override def isUnreferred() = true
      override def isIsolated() = true
    }
  }

  protected def getSubPackageByRelativeName(relativeName: String) = {

    subPackages.find(_.getReference().equals(reference.child(relativeName))) match {
      case Some(subPackage) => subPackage
      case None =>
        val directSubPackage = createNew(reference.createChild(relativeName))
        subPackages.add(directSubPackage)
        directSubPackage
    }
  }

  protected def notAllAreDescendantsOf(packages: java.util.List[PackageReference], possibleAncestor: PackageReference) = packages.exists(!_.isDescendantOf(possibleAncestor))

  protected def isDirectSuperPackageOf(aPackage: Package) = reference.isDirectParentOf(aPackage.getReference())

  protected def doesNotContain(aPackage: Package) = !aPackage.getReference().pointsInside(reference)

  protected def firstPartOfRelativeNameTo(parentPackage: Package) = reference.firstPartOfRelativeNameTo(parentPackage.getReference())

  protected def notEveryNodeUnderFirst(cycleCandidate: java.util.List[PackageReference]) = {
    val first = cycleCandidate.head
    cycleCandidate.tail.find(!_.isDescendantOf(first)) match {
      case None => first.equals(reference)
      case Some(_) => true
    }
  }

  protected def isValid(cycleCandidate: java.util.List[PackageReference]) =
    if (cycleCandidate.size() < 2) false
    else notEveryNodeUnderFirst(cycleCandidate)

  protected def insertIndirectSubPackage(aPackage: Package) = {
    val relativeNameOfDirectSubPackage = aPackage.firstPartOfRelativeNameTo(this.asInstanceOf[Package])
    getSubPackageByRelativeName(relativeNameOfDirectSubPackage).insert(aPackage)
  }

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