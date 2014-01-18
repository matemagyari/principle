package org.tindalos.principle.domain.core

import scala.collection.mutable.ListBuffer
import org.apache.commons.lang3.builder.HashCodeBuilder
import com.google.common.collect.Lists
import scala.collection.JavaConversions._
import com.google.common.collect.Sets
import com.google.common.base.Optional

abstract class PackageScala(val reference: PackageReference) {

  val subPackages: java.util.List[Package] = Lists.newArrayList()

  def this(referenceName: String) = this(new PackageReference(referenceName))

  def getReference() = reference

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

  protected def accumulatedDirectlyReferredPackages(packageReferenceMap: java.util.Map[PackageReference, Package]): java.util.Set[Package] =
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

  protected def indexInTraversedPath(traversedPackages: java.util.List[PackageReference]) = {
    val index = traversedPackages.indexOf(reference)
    if (index != -1) index
    else {
      var matchFoundIndex: Option[Int] = None
      for (index <- 0 to traversedPackages.size() - 1 if matchFoundIndex.isEmpty) {
        val possibleMatch = traversedPackages.get(index)
        if (possibleMatch.equals(reference)
          || (reference.isDescendantOf(possibleMatch)
            && notAllAreDescendantsOf(traversedPackages.subList(index + 1, traversedPackages.size()), possibleMatch))) {
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

  def detectCycles(packageReferences: java.util.Map[PackageReference, Package]): CyclesInSubgraph =
    detectCyclesOnThePathFromHere(TraversedPackages.empty(), CyclesInSubgraph.empty(), packageReferences)

  // it dies if there are cycles
  // through references, not through subPackages. transaitive too
  def cumulatedDependencies(packageReferenceMap: java.util.Map[PackageReference, Package]) = cumulatedDependenciesAcc(packageReferenceMap, new java.util.HashSet[PackageReference]())

  private def cumulatedDependenciesAcc(packageReferenceMap: java.util.Map[PackageReference, Package], dependencies: java.util.Set[PackageReference]): java.util.Set[PackageReference] = {

    val accumulatedPackageReferences = this.accumulatedDirectPackageReferences()

    accumulatedPackageReferences.removeAll(dependencies)

    if (accumulatedPackageReferences.isEmpty()) {
      dependencies.remove(reference)
      dependencies
    } else {
      val result = Sets.newHashSet(accumulatedPackageReferences)
      accumulatedPackageReferences.foreach({ packageReference =>
        dependencies.add(packageReference)
        result.addAll(packageReferenceMap.get(packageReference).cumulatedDependenciesAcc(packageReferenceMap, dependencies))
        result.remove(reference)
      })
      result
    }
  }

  protected def detectCyclesOnThePathFromHere(
    traversedPackages: TraversedPackages,
    foundCycles: CyclesInSubgraph,
    packageReferences: java.util.Map[PackageReference, Package]): CyclesInSubgraph = {

    //enough cycles have been found already with this package
    if (foundCycles.isBreakingPoint(this.asInstanceOf[Package])) foundCycles
    else {
      foundCycles.rememberPackageAsInvestigated(this.asInstanceOf[Package])

      // if we just closed a cycle, add it to the found list then return
      val cycleCandidateEndingHere = findCycleCandidateEndingHere(traversedPackages)
      if (cycleCandidateEndingHere.isDefined) {
        if (isValid(cycleCandidateEndingHere.get)) {
          foundCycles.add(new Cycle(ListConverter.convert(cycleCandidateEndingHere.get)))
        }
      } else {
        accumulatedDirectlyReferredPackages(packageReferences).foreach({ referencedPackage =>
          val cyclesInSubgraph = referencedPackage.asInstanceOf[PackageScala].detectCyclesOnThePathFromHere(traversedPackages.add(reference), foundCycles, packageReferences)
          foundCycles.mergeIn(cyclesInSubgraph)
        })
      }
      //System.err.println("Cycles found so far: " + foundCycles.getCycles().size())
      foundCycles
    }
  }
  private def findCycleCandidateEndingHere(traversedPackages: TraversedPackages): Option[java.util.List[PackageReference]] = {

    val indexOfThisPackage = indexInTraversedPath(ListConverter.convert(traversedPackages.packages))
    if (indexOfThisPackage > -1) {
      val cycleCandidate = ListConverter.convert(traversedPackages.from(indexOfThisPackage))
      Some(cycleCandidate)
    } else None
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