package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.detector.structure.Structure.{NodeGroup, cohesionDelta, merge}

object CohesiveGroupsDiscoveryModule {

  def collapseToLimit(components: Set[NodeGroup]): Set[NodeGroup] = {
    val closest = findClosestPair(components, cohesionDelta)
    //printStatistics(components)
    printPair(closest)

    if (closest._3 > 0.1) {
      val merged = merge(closest._1, closest._2)
      val newComponents = components.filter(c => c != closest._1 && c != closest._2) + merged
      collapseToLimit(newComponents)
    } else components
  }

  def printStatistics(components: Set[NodeGroup]) = {
    println("Num of components: " + components.size)
    println("Num of dependencies: " + components.toList.map(_.externalDependencies.size).foldLeft(0)(_ + _))
    println("Num of dependants: " + components.toList.map(_.externalDependants.size).foldLeft(0)(_ + _))
  }

  def printPair(p: (NodeGroup, NodeGroup, Double)) {
    println("Closest 1 " + p._1.cohesion() + " " + p._1.nodes.foldLeft("")(_ + "," + _.id))
    println("Closest 2 " + p._2.cohesion() + " " + p._2.nodes.foldLeft("")(_ + "," + _.id))
    println("Closest value " + p._3)
  }


  def findClosestPair(s: Set[NodeGroup], closeness: (NodeGroup, NodeGroup) => Double): (NodeGroup, NodeGroup, Double) = {
    val pairs = for {
      c1 <- s
      c2 <- s if c1 != c2 && c1.hashCode() <= c2.hashCode()
    } yield (c1, c2, closeness(c1, c2))
    pairs.maxBy(_._3)
  }


}
