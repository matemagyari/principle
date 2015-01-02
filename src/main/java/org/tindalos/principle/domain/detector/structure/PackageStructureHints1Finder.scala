package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.detector.structure.Graph._

object PackageStructureHints1Finder {

  case class GroupingResult(val grouping: Map[Set[String], List[NodeId]], val labelledSources: List[(String, NodeId)])

  def makeGroups(graph: Set[Node]) = {
    val sources = Graph.findSources(graph).toList.sortBy(_.id)
    val labelledSources = for {i <- 0 to sources.size - 1} yield (sources(i), label(sources.size, i))

    val labelledNodes: IndexedSeq[(String, NodeId)] =
      for {(source, label) <- labelledSources
           downstream <- Graph.findDownstreamNodes(source, graph)} yield (label, downstream.id)

    val x1: Map[NodeId, List[(String, NodeId)]] = labelledNodes
      .toList
      .groupBy(_._2)

    val x2: Map[NodeId, Set[String]] = x1.map(kv => (kv._1, kv._2.map(_._1).toSet))

    val g = x2.toList.groupBy(_._2).map(kv => (kv._1, kv._2.map(_._1)))

    GroupingResult(g, labelledSources.toList.map(x => (x._2, x._1.id)))

  }

  def label(max: Int, i: Int) = {
    "".r
    val postfix =
      if (max < 10) i.toString
      else if (max < 100)
        if (i < 10) "0" + i
        else i.toString
      else if (max < 1000)
        if (i < 10) "00" + i
        else if (i < 100) "0" + i
        else i.toString
    "s" + postfix
  }

}
