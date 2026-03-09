package org.tindalos.principle.domain.analyzers.structure

object PackageStructureHints1Finder {

  case class GroupingResult(grouping: Map[Set[String], List[String]], labelledSources: List[(String, String)])

  def makeGroups(graph: Set[Node]): GroupingResult =  {
    val sources = Graph.findSources(graph).toList.sortBy(_.id)
    val labelledSources = for {i <- 0 to sources.size - 1} yield (sources(i), label(sources.size, i))

    val labelledNodes: IndexedSeq[(String, String)] =
      for {(source, label) <- labelledSources
           downstream <- Graph.findDownstreamNodes(source, graph)
      } yield (label, downstream.id)

    val grouping:Map[Set[String], List[String]] = labelledNodes
      .toList
      .groupBy(_._2)                             //Map[NodeId, List[(String, NodeId)]]
      .map(kv => (kv._1, kv._2.map(_._1).toSet)) //Map[NodeId, Set[String]]
      .toList                                    //List[(NodeId, Set[String])]
      .groupBy(_._2)                             //Map[NodeId, List[(NodeId, Set[String]])]
      .map(kv => (kv._1, kv._2.map(_._1)))


    GroupingResult(grouping, labelledSources.toList.map(x => (x._2, x._1.id)))

  }

  def label(max: Int, i: Int) = {
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
