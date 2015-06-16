package org.tindalos.principle.domain.core

import scala.collection.mutable.Set
import scala.collection.mutable.Map

class CyclesInSubgraph {

  private val investigatedPackages_ = scala.collection.mutable.Set[Package]()
  private val breakingPoints = scala.collection.mutable.Map[PackageReference, scala.collection.mutable.Set[Cycle]]()

  def investigatedPackages = investigatedPackages_.toSet

  def cycles: scala.collection.immutable.Map[PackageReference, scala.collection.immutable.Set[Cycle]] = (for ((k, v) <- breakingPoints) yield (k, v.toSet)).toMap

  def add(cycle: Cycle) = {
    //if this Cycle hasn't been registered yet
    if (!breakingPoints.values.exists(_.contains(cycle)))
      breakingPoints.get(cycle.end) match {
        case Some(opt) => opt += cycle
        case None => breakingPoints += cycle.end -> Set[Cycle](cycle)
      }
  }

  def rememberPackageAsInvestigated(aPackage: Package) = investigatedPackages_ += aPackage

  def mergeIn(that: CyclesInSubgraph) = {
    that.breakingPoints.values.toSet.flatten.foreach({ add(_) })
    investigatedPackages_ ++= that.investigatedPackages
  }

  def mergeBreakingPoints2(breakingPointsInOther: scala.collection.immutable.Map[PackageReference, scala.collection.immutable.Set[Cycle]]) = {
    breakingPointsInOther.values.toSet.flatten.foreach({ add(_) })
    cycles
  }

  def isBreakingPoint(aPackage: Package) = breakingPoints.get(aPackage.reference) match {
    case Some(cyclesForThisBreakingPoint) => cyclesForThisBreakingPoint.size > CyclesInSubgraph.LIMIT
    case None => false
  }

  override def toString() = "CyclesInSubgraph [cycles=" + breakingPoints + ", investigatedPackages=" + investigatedPackages + "]"

}

object CyclesInSubgraph {

  val LIMIT = 5

  def empty() = new CyclesInSubgraph()

}