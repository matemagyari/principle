package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.detector.structure.Structure.{Component, NodeId, Node}

object StructureFinder {

  def nodeToComponent(c: Node) = Component(Set(c.name), c.dependencies, c.dependants)

  def nodesToComponent(ns: Set[Node]) =
    Component(ns.map(_.name),
      ns.flatMap(_.dependencies),
      ns.flatMap(_.dependants))

  def collapseToLimit(components: Set[Component], nodes: Set[Node]): Set[Component] = {
    val cohesionFn = (c1: Component, c2: Component) => c1.cohesionDelta(c2, nodes)
    val cohesionFn2 = (c1: Component, c2: Component) => c1.gravityTo(c2)
    val closest = findClosest(components, cohesionFn2)
    //printStatistics(components)
    println("Closest 1" + closest._1.nodes)
    println("Closest 2" + closest._2.nodes)
    println("Closest value" + closest._3)
    if (closest._3 > 0.1) {
      val merged = closest._1.merge(closest._2)
      val newComponents = components.filter(c => c != closest._1 && c != closest._2) + merged
      collapseToLimit(newComponents, nodes)
    } else components
  }

  def printStatistics(components: Set[Component]) = {
    println("Num of components: " + components.size)
    println("Num of dependencies: " + components.toList.map(_.dependencies.size).foldLeft(0)(_ + _))
    println("Num of dependants: " + components.toList.map(_.dependants.size).foldLeft(0)(_ + _))
  }


  def findClosest(s: Set[Component], closeness: (Component, Component) => Double): (Component, Component, Double) = {
    val pairs = for {
      c1 <- s
      c2 <- s if c1 != c2
    } yield (c1, c2, closeness(c1, c2))
    pairs.maxBy(_._3)
  }


}
