package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.detector.structure.Structure.{NodeGroup, cohesionDelta, merge}

import scala.collection.SortedMap

object CohesiveGroupsDiscoveryModule {

  //imperative but fast
  def collapseToLimit(initialComponents: Set[NodeGroup]): Set[NodeGroup] = {

    def aPair(n1: NodeGroup, n2: NodeGroup) = ((n1, n2), cohesionDelta(n1, n2))

    val pairs = for {
      n1 <- initialComponents.toList
      n2 <- initialComponents.toList if (n1 != n2 && n1.hashCode() <= n2.hashCode())
    } yield aPair(n1, n2)

    var pairMap: Map[(NodeGroup, NodeGroup), Double] = pairs.toMap

    var ns = initialComponents
    var max = pairMap.maxBy(_._2)
    while ( max._2 > 0.1) {
      //printPair(max)
      val merged = merge(max._1._1, max._1._2)
      ns = ns - max._1._1 - max._1._2 + merged

      val newPairs = for {
        n <- ns if n != merged
      } yield aPair(n, merged)

      def overlap(n:((NodeGroup, NodeGroup),Double)) = Set(n._1._1, n._1._2,max._1._1, max._1._2).size < 4

      //var start = System.currentTimeMillis()
      pairMap = pairMap.filterNot( kv => overlap(kv) ) ++ newPairs
      //println("Map update " + (System.currentTimeMillis() - start))
      max = pairMap.maxBy(_._2)
    }
    ns
  }

  //functional but slow
  def collapseToLimitFP(components: Set[NodeGroup]): Set[NodeGroup] = {
    val closest = findClosestPair(components, cohesionDelta)
    //printStatistics(components)
    printPair(closest)

    if (closest._3 > 0.1) {
      val merged = merge(closest._1, closest._2)
      val newComponents = components.filter(c => c != closest._1 && c != closest._2) + merged
      collapseToLimitFP(newComponents)
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

  def printPair(p: ((NodeGroup, NodeGroup), Double)) {
    println("Closest 1 " + p._1._1.cohesion() + " " + p._1._1.nodes.foldLeft("")(_ + "," + _.id))
    println("Closest 2 " + p._1._2.cohesion() + " " + p._1._2.nodes.foldLeft("")(_ + "," + _.id))
    println("Closest value " + p._2)
  }


  def findClosestPair(s: Set[NodeGroup], closeness: (NodeGroup, NodeGroup) => Double): (NodeGroup, NodeGroup, Double) = {

    val pairs = for {
      c1 <- s
      c2 <- s if c1 != c2 && c1.hashCode() <= c2.hashCode()
    } yield (c1, c2, closeness(c1, c2))

    pairs.maxBy(_._3)
  }


}
