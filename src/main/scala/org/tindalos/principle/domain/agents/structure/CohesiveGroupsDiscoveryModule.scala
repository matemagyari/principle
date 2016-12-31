package org.tindalos.principle.domain.agents.structure

import org.tindalos.principle.domain.agents.structure.Structure.{NodeGroup, cohesionDelta, merge}

import scala.annotation.tailrec

object CohesiveGroupsDiscoveryModule {

  def collapseToLimit(initialComponents: Set[NodeGroup]): Set[NodeGroup] = {

    val pairs = for {
      n1 <- initialComponents.toList
      n2 <- initialComponents.toList if (n1 != n2 && n1.hashCode() <= n2.hashCode())
    } yield aPair(n1, n2)

    collapse(initialComponents, pairs.toMap)
  }

  @tailrec
  private def collapse(ns: Set[NodeGroup], pairMap: Map[(NodeGroup, NodeGroup), Double]): Set[NodeGroup] = {
    val max = pairMap.maxBy(_._2)
    if (max._2 > 0.1) {
      val merged = merge(max._1._1, max._1._2)
      val updatedNs = ns - max._1._1 - max._1._2 + merged

      val newPairs = for {
        n <- updatedNs if n != merged
      } yield aPair(n, merged)

      def overlap(n: ((NodeGroup, NodeGroup), Double)) = Set(n._1._1, n._1._2, max._1._1, max._1._2).size < 4

      val updatedPairMap = pairMap.filterNot(kv => overlap(kv)) ++ newPairs

      collapse(updatedNs, updatedPairMap)
    }
    else ns
  }

  private def aPair(n1: NodeGroup, n2: NodeGroup) = ((n1, n2), cohesionDelta(n1, n2))

}
