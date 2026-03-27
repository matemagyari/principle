package org.tindalos.principle.domain.core

import org.apache.commons.lang3.builder.HashCodeBuilder
import org.tindalos.principle.domain.core.packages.{PackageMetrics, PackageReference, PackageWithMetrics}

import scala.collection.JavaConverters._
import java.util.{Collections, Optional}
import java.util.stream.Collectors

abstract class Package(val reference: PackageReference) extends PackageWithMetrics {

  val _subPackages: java.util.List[Package] = new java.util.ArrayList[Package]()

  def subPackages: java.util.List[Package] = java.util.Collections.unmodifiableList(_subPackages)
  
  def this(referenceName: String) = this(new PackageReference(referenceName))

  override def getMetrics(): PackageMetrics
  override def getOwnPackageReferences(): java.util.Set[PackageReference]
  override def getOwnExternalPackageReferences(): java.util.Set[PackageReference]
  // all the references going out from this package
  override def accumulatedDirectPackageReferences(): java.util.Set[PackageReference] =
    java.util.stream.Stream
        .concat(
          _subPackages
            .stream()
            .flatMap(_.accumulatedDirectPackageReferences().stream())
            .filter(x => !x.equals(reference)),
          getOwnPackageReferences().stream()
        )
        .collect(Collectors.toUnmodifiableSet())

  def isUnreferred(): Boolean

  def toMap(): java.util.Map[PackageReference, Package] = toMap(scala.collection.mutable.Map[PackageReference, Package]()).asJava

  def detectCycles(packageReferences: java.util.Map[PackageReference, Package]): CyclesInSubgraph =
    detectCyclesOnThePathFromHere(TraversedPackages.empty(), CyclesInSubgraph.empty(), Collections.unmodifiableMap(packageReferences))

  // it dies if there are cycles
  // through references, not through subPackages. transaitive too
  def cumulatedDependencies(packageReferenceMap: java.util.Map[PackageReference, Package]): java.util.Set[PackageReference] =
    cumulatedDependenciesAcc(packageReferenceMap, new java.util.HashSet[PackageReference]())


  def insert(aPackage: Package): Unit = {
    if (this.equals(aPackage)) {
      throw new PackageStructureBuildingException("Attempted to insert into itself " + this)
    } else if (doesNotContain(aPackage)) {
      throw new PackageStructureBuildingException("Attempted to insert " + aPackage + " into " + this)
    } else if (isDirectSuperPackageOf(aPackage)) {
      _subPackages.add(aPackage)
    } else {
      insertIndirectSubPackage(aPackage)
    }
  }

  private def accumulatedDirectlyReferredPackages(packageReferenceMap: java.util.Map[PackageReference, Package]): java.util.Set[Package] = {
    accumulatedDirectPackageReferences().stream()
      .flatMap(r => Optional.ofNullable(packageReferenceMap.get(r)).stream())
      .collect(Collectors.toUnmodifiableSet[Package])
  }

 
  private def toMap(accumulatingMap: scala.collection.mutable.Map[PackageReference, Package]): Map[PackageReference, Package] = {

    accumulatingMap.put(reference, this)
    _subPackages.forEach(child => child.toMap(accumulatingMap))
    accumulatingMap.toMap
  }

  private def getSubPackageByRelativeName(relativeName: String): Package = {
    val targetReference = reference.child(relativeName)
    _subPackages.stream()
      .filter(subPackage => subPackage.reference.equals(targetReference))
      .findFirst()
      .orElseGet(() => {
        val directSubPackage = new Package(reference.createChild(relativeName)) {
          override def getOwnPackageReferences() = java.util.Collections.emptySet[PackageReference]()
          override def getOwnExternalPackageReferences() = java.util.Collections.emptySet[PackageReference]()
          override def getMetrics() = PackageMetrics.UNDEFINED
          override def isUnreferred() = true
        }

        _subPackages.add(directSubPackage)
        directSubPackage
      })
  }

  private def indexInTraversedPath(traversedPackages: List[PackageReference]) = {
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

 
  private def cumulatedDependenciesAcc(
    packageReferenceMap: java.util.Map[PackageReference, Package],
    dependencies: java.util.Set[PackageReference]): java.util.Set[PackageReference] = {

    val accumulatedPackageReferences = this.accumulatedDirectPackageReferences().stream()
      .filter(packageReference => !dependencies.contains(packageReference))
      .collect(Collectors.toUnmodifiableSet[PackageReference])

    if (accumulatedPackageReferences.isEmpty) {
      dependencies.stream()
        .filter(packageReference => !packageReference.equals(reference))
        .collect(Collectors.toUnmodifiableSet[PackageReference])
    } else {
      val result = new java.util.HashSet[PackageReference](accumulatedPackageReferences)
      accumulatedPackageReferences.forEach { packageReference =>
        dependencies.add(packageReference)
        result.addAll(packageReferenceMap.get(packageReference).cumulatedDependenciesAcc(packageReferenceMap, dependencies))
        result.remove(reference)
      }
      java.util.Set.copyOf(result)
    }
  }

  private def detectCyclesOnThePathFromHere(
    traversedPackages: TraversedPackages, 
    foundCycles: CyclesInSubgraph, 
    packageReferences: java.util.Map[PackageReference, Package]): CyclesInSubgraph = {

    //enough cycles have been found already with this package
    if (foundCycles.isBreakingPoint(this)) foundCycles
    else {
      foundCycles.rememberPackageAsInvestigated(this)

      // if we just closed a cycle, add it to the found list then return
      val cycleCandidateEndingHere = findCycleCandidateEndingHere(traversedPackages)
      if (cycleCandidateEndingHere.isDefined) {
        if (isValid(cycleCandidateEndingHere.get)) {
          val list: java.util.List[PackageReference] = cycleCandidateEndingHere.getOrElse(List.empty).asJava
          foundCycles.add(new Cycle(list))
        }
      } else {
        accumulatedDirectlyReferredPackages(packageReferences).forEach({ referencedPackage =>
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

  private def notAllAreDescendantsOf(packages: List[PackageReference], possibleAncestor: PackageReference) = packages.exists(!_.isDescendantOf(possibleAncestor))

  private def isDirectSuperPackageOf(aPackage: Package) = reference.isDirectParentOf(aPackage.reference)

  private def doesNotContain(aPackage: Package) = !aPackage.reference.pointsInside(reference)

  private def firstPartOfRelativeNameTo(parentPackage: Package) = reference.firstPartOfRelativeNameTo(parentPackage.reference)

  private def notEveryNodeUnderFirst(cycleCandidate: List[PackageReference]): Boolean = {
    val first = cycleCandidate.head
    cycleCandidate.tail.find(!_.isDescendantOf(first)) match {
      case None => first.equals(reference)
      case Some(_) => true
    }
  }

  private def isValid(cycleCandidate: List[PackageReference]): Boolean =
    if (cycleCandidate.length < 2) false
    else notEveryNodeUnderFirst(cycleCandidate)

  private def insertIndirectSubPackage(aPackage: Package): Unit = {
    val relativeNameOfDirectSubPackage = aPackage.firstPartOfRelativeNameTo(this)
    getSubPackageByRelativeName(relativeNameOfDirectSubPackage).insert(aPackage)
  }

  override def equals(other: Any) = other.isInstanceOf[Package] && other.asInstanceOf[Package].reference.equals(reference)

  override def hashCode() = new HashCodeBuilder().append(reference).hashCode()

  override def toString() = reference.toString()
}

private class TraversedPackages(val packages: List[PackageReference] = List()) {

  def add(reference: PackageReference) = new TraversedPackages(packages :+ reference)
  def from(index: Int): List[PackageReference] = packages.slice(index, packages.length)
}

private object TraversedPackages {
  def empty() = new TraversedPackages()
}