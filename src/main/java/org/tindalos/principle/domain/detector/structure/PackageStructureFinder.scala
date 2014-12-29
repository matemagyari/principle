package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.detector.structure.Graph.{Node, NodeId}

object PackageStructureFinder {

  def merge[K, V](collisionFn: List[V] => V, m1: Map[K, V], m2: Map[K, V]): Map[K, V] = {
    val s = m1.to[List] ++ m2.to[List]
    s.groupBy(_._1).map(kv => (kv._1, collisionFn(kv._2.map(_._2))))
  }

  def merge[K, V](collisionFn: List[V] => V, ms: List[Map[K, V]]): Map[K, V] = {
    ms.foldLeft(List[(K, V)]())(_ ++ _.to[List])
      .groupBy(_._1)
      .map(kv => (kv._1, collisionFn(kv._2.map(_._2))))
  }

  def makeGroups(graph: Set[Node]):Map[Set[String],List[NodeId]] = {
    val sources = Graph.findSources(graph).toList
    val labelledSources = for {i <- 0 to sources.size-1} yield (sources(i), "s" + i)

    val labelledNodes: IndexedSeq[(String, NodeId)] =
      for {(source, label) <- labelledSources
           downstream <- Graph.findDownstreamNodes(source, graph)} yield (label, downstream.id)

    val x1:Map[NodeId,List[(String,NodeId)]] = labelledNodes
      .toList
      .groupBy(_._2)

    val x2:Map[NodeId,Set[String]] = x1.map( kv => (kv._1, kv._2.map(_._1).toSet))

    x2.toList.groupBy(_._2).map(kv => (kv._1, kv._2.map(_._1)))



  }
}
