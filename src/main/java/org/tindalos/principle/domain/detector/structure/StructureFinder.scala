package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.detector.structure.Structure.{NodeGroup, NodeId, Node}

object StructureFinder {

  def collapseToLimit(components: Set[NodeGroup]): Set[NodeGroup] = {
    val cohesionFn = (c1: NodeGroup, c2: NodeGroup) => c1.cohesionDelta(c2)
    val cohesionFn2 = (c1: NodeGroup, c2: NodeGroup) => c1.gravityBetween(c2)
    val closest = findClosestPair(components, cohesionFn)
    //printStatistics(components)
    printPair(closest)

    if (closest._3 > 0.2) {
      val merged = closest._1.merge(closest._2)
      val newComponents = components.filter(c => c != closest._1 && c != closest._2) + merged
      collapseToLimit(newComponents)
    } else components
  }

  def printStatistics(components: Set[NodeGroup]) = {
    println("Num of components: " + components.size)
    println("Num of dependencies: " + components.toList.map(_.externalDependencies.size).foldLeft(0)(_ + _))
    println("Num of dependants: " + components.toList.map(_.externalDependants.size).foldLeft(0)(_ + _))
  }

  def printPair(p:(NodeGroup,NodeGroup,Double)) {
    println("Closest 1 " + p._1.nodes.foldLeft("")(_+ "," +_.id))
    println("Closest 2 " + p._2.nodes.foldLeft("")(_+ "," +_.id))
    println("Closest value " + p._3)
  }


  def findClosestPair(s: Set[NodeGroup], closeness: (NodeGroup, NodeGroup) => Double): (NodeGroup, NodeGroup, Double) = {
    val pairs = for {
      c1 <- s
      c2 <- s if c1 != c2
    } yield (c1, c2, closeness(c1, c2))
    pairs.maxBy(_._3)
  }


}
