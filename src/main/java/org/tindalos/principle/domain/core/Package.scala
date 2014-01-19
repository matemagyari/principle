package org.tindalos.principle.domain.core

import scala.collection.mutable.ListBuffer
import org.apache.commons.lang3.builder.HashCodeBuilder
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import com.google.common.base.Optional

abstract class Package(val reference: PackageReference) {

  val _subPackages: ListBuffer[Package] = ListBuffer()

  def subPackages = _subPackages.toList
  
  def this(referenceName: String) = this(new PackageReference(referenceName))

  def isUnreferred(): Boolean
  def getMetrics(): Metrics
  def getOwnPackageReferences(): Set[PackageReference]
  def instability = getMetrics().instability
  def distance = getMetrics().distance
  def isIsolated() = getMetrics().afferentCoupling == 0 && getMetrics().efferentCoupling == 0

  def insert(aPackage: Package) {
    if (this.equals(aPackage)) {
      throw new PackageStructureBuildingException("Attempted to insert into itself " + this)
    } else if (doesNotContain(aPackage)) {
      throw new PackageStructureBuildingException("Attempted to insert " + aPackage + " into " + this)
    } else if (isDirectSuperPackageOf(aPackage)) {
      _subPackages += aPackage
    } else {
      insertIndirectSubPackage(aPackage)
    }
  }

  // all the references going out from this package
  def accumulatedDirectPackageReferences(): Set[PackageReference] =
    subPackages.flatMap(_.accumulatedDirectPackageReferences()).toSet.filterNot(_.equals(reference)).toSet ++: subPackages.flatMap(_.accumulatedDirectPackageReferences()).toSet.filterNot(_.equals(reference)).toSet ++: getOwnPackageReferences()

  protected def accumulatedDirectlyReferredPackages(packageReferenceMap: Map[PackageReference, Package]): Set[Package] =
    accumulatedDirectPackageReferences().map(packageReferenceMap.get(_).get)

  def toMap(): Map[PackageReference, Package] = toMap(scala.collection.mutable.Map[PackageReference, Package]())

  protected def toMap(accumulatingMap: scala.collection.mutable.Map[PackageReference, Package]): Map[PackageReference, Package] = {

    accumulatingMap.put(reference, this)
    subPackages.foreach(child => child.toMap(accumulatingMap))
    accumulatingMap.toMap
  }

  protected def createNew(name: String) = {
    new Package(name) {
      override def getOwnPackageReferences() = Set()
      override def getMetrics() = UndefinedMetrics
      override def isUnreferred() = true
      override def isIsolated() = true
    }
  }

  protected def getSubPackageByRelativeName(relativeName: String) = {

    subPackages.find(_.reference.equals(reference.child(relativeName))) match {
      case Some(subPackage) => subPackage
      case None =>
        val directSubPackage = createNew(reference.createChild(relativeName))
        _subPackages += directSubPackage
        directSubPackage
    }
  }

  protected def indexInTraversedPath(traversedPackages: List[PackageReference]) = {
    val index = traversedPackages.indexOf(reference)
    if (index != -1) index
    else {
      var matchFoundIndex: Option[Int] = None
      for (index <- 0 to traversedPackages.length - 1 if matchFoundIndex.isEmpty) {
        val possibleMatch = traversedPackages(index)
        if (possibleMatch.equals(reference)
          || (reference.isDescendantOf(possibleMatch)
            && notAllAreDescendantsOf(traversedPackages.slice(index + 1, traversedPackages.length), possibleMatch))) {
          matchFoundIndex = Some(index)
        }

      }
      // System.err.println("Failed " + traversedPackages + " " + this)
      matchFoundIndex match {
        case None => -1
        case Some(index) => index
      }
    }
  }

  def detectCycles(packageReferences: Map[PackageReference, Package]) =
    detectCyclesOnThePathFromHere(TraversedPackages.empty(), CyclesInSubgraph.empty(), packageReferences)

  // it dies if there are cycles
  // through references, not through subPackages. transaitive too
  def cumulatedDependencies(packageReferenceMap: Map[PackageReference, Package]) = cumulatedDependenciesAcc(packageReferenceMap, scala.collection.mutable.Set[PackageReference]())

  private def cumulatedDependenciesAcc(packageReferenceMap: Map[PackageReference, Package], dependencies: scala.collection.mutable.Set[PackageReference]): Set[PackageReference] = {

    val accumulatedPackageReferences = this.accumulatedDirectPackageReferences().filterNot(dependencies.contains(_))

    if (accumulatedPackageReferences.isEmpty) {
      dependencies.filterNot(_.equals(reference)).toSet
    } else {
      var result = accumulatedPackageReferences
      accumulatedPackageReferences.foreach({ packageReference =>
        dependencies.add(packageReference)
        result = result ++: (packageReferenceMap.get(packageReference).get.cumulatedDependenciesAcc(packageReferenceMap, dependencies))
        result = result.filterNot(_.equals(reference))
      })
      result
    }
  }

  protected def detectCyclesOnThePathFromHere(
    traversedPackages: TraversedPackages, 
    foundCycles: CyclesInSubgraph, 
    packageReferences: Map[PackageReference, Package]): CyclesInSubgraph = {

    //enough cycles have been found already with this package
    if (foundCycles.isBreakingPoint(this)) foundCycles
    else {
      foundCycles.rememberPackageAsInvestigated(this)

      // if we just closed a cycle, add it to the found list then return
      val cycleCandidateEndingHere = findCycleCandidateEndingHere(traversedPackages)
      if (cycleCandidateEndingHere.isDefined) {
        if (isValid(cycleCandidateEndingHere.get)) {
          foundCycles.add(new Cycle(cycleCandidateEndingHere.get))
        }
      } else {
        accumulatedDirectlyReferredPackages(packageReferences).foreach({ referencedPackage =>
        val cyclesInSubgraph = referencedPackage.detectCyclesOnThePathFromHere(traversedPackages.add(reference), foundCycles, packageReferences)
        foundCycles.mergeIn(cyclesInSubgraph)
        })
      }
      //System.err.println("Cycles found so far: " + foundCycles.getCycles().size())
      foundCycles
    }
  }
  private def findCycleCandidateEndingHere(traversedPackages: TraversedPackages): Option[List[PackageReference]] = {

    val indexOfThisPackage = indexInTraversedPath(traversedPackages.packages)
    if (indexOfThisPackage > -1) Some(traversedPackages.from(indexOfThisPackage))
    else None
  }

  protected def notAllAreDescendantsOf(packages: List[PackageReference], possibleAncestor: PackageReference) = packages.exists(!_.isDescendantOf(possibleAncestor))

  protected def isDirectSuperPackageOf(aPackage: Package) = reference.isDirectParentOf(aPackage.reference)

  protected def doesNotContain(aPackage: Package) = !aPackage.reference.pointsInside(reference)

  protected def firstPartOfRelativeNameTo(parentPackage: Package) = reference.firstPartOfRelativeNameTo(parentPackage.reference)

  protected def notEveryNodeUnderFirst(cycleCandidate: List[PackageReference]) = {
    val first = cycleCandidate.head
    cycleCandidate.tail.find(!_.isDescendantOf(first)) match {
      case None => first.equals(reference)
      case Some(_) => true
    }
  }

  protected def isValid(cycleCandidate: List[PackageReference]) =
    if (cycleCandidate.length < 2) false
    else notEveryNodeUnderFirst(cycleCandidate)

  protected def insertIndirectSubPackage(aPackage: Package) = {
    val relativeNameOfDirectSubPackage = aPackage.firstPartOfRelativeNameTo(this)
    getSubPackageByRelativeName(relativeNameOfDirectSubPackage).insert(aPackage)
  }

  override def equals(other: Any) = other.isInstanceOf[Package] && other.asInstanceOf[Package].reference.equals(reference)

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