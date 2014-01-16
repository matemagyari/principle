package org.tindalos.principle.domain.core

import scala.collection.mutable.Set
import scala.collection.mutable.Map

class CyclesInSubgraph {

  val investigatedPackages_ = Set[Package]()
  val breakingPoints = Map[PackageReference, Set[Cycle]]()

  def investigatedPackages(): java.util.Set[Package] = ListConverter.convert(investigatedPackages_)

  def cycles() = {
    val cycles:java.util.Map[PackageReference, java.util.Set[Cycle]] = new java.util.HashMap[PackageReference, java.util.Set[Cycle]]()
    for ((k, v) <- breakingPoints) {
      cycles.put(k, ListConverter.convert(v))
    }
    cycles
  }

  def add(cycle: Cycle) = breakingPoints.get(cycle.getLast()) match {
    case Some(opt) => opt += cycle
    case None => breakingPoints += cycle.getLast() -> Set[Cycle](cycle)
  }

  def rememberPackageAsInvestigated(aPackage: Package) = investigatedPackages.add(aPackage)

  def mergeIn(that: CyclesInSubgraph) = {
    mergeBreakingPoints(that.breakingPoints)
    investigatedPackages.addAll(that.investigatedPackages)
  }

  def mergeBreakingPoints(breakingPointsInOther: Map[PackageReference, Set[Cycle]]) =
    for ((k, v) <- breakingPointsInOther) {
      breakingPoints.get(k) match {
        case Some(cyclesForThisBreakingPoint) => cyclesForThisBreakingPoint ++= v
        case None => breakingPoints += k -> v
      }
    }

  def isBreakingPoint(aPackage: Package) = breakingPoints.get(aPackage.getReference()) match {
    case Some(cyclesForThisBreakingPoint) => cyclesForThisBreakingPoint.size > CyclesInSubgraph.LIMIT
    case None => false
  }

  override def toString() = "CyclesInSubgraph [cycles=" + breakingPoints + ", investigatedPackages=" + investigatedPackages + "]"

}

object CyclesInSubgraph {

  val LIMIT = 25

  def empty() = new CyclesInSubgraph()

}