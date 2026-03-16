package org.tindalos.principle.domain.analyzers.structure

import org.tindalos.principle.domain.analyzers.structure.GroupingResult.LabelledSource
import scala.collection.JavaConverters._

object PackageStructureHints1Finder {

  def makeGroups(graph: Set[Node]): GroupingResult = {
    val sources = Graph.findSources(graph.asJava).asScala.toList.sortBy(_.id)
    val labelledSources = for {i <- 0 to sources.size - 1} yield (sources(i), label(sources.size, i))

    val labelledNodes: IndexedSeq[(String, String)] =
      for {(source, lbl) <- labelledSources
           downstream <- Graph.findDownstreamNodes(source, graph.asJava).asScala
      } yield (lbl, downstream.id)

    val grouping: Map[Set[String], List[String]] = labelledNodes
      .toList
      .groupBy(_._2)                              //Map[NodeId, List[(String, NodeId)]]
      .map(kv => (kv._1, kv._2.map(_._1).toSet)) //Map[NodeId, Set[String]]
      .toList                                     //List[(NodeId, Set[String])]
      .groupBy(_._2)                              //Map[Set[String], List[(NodeId, Set[String])]]
      .map(kv => (kv._1, kv._2.map(_._1)))

    val javaGrouping: java.util.Map[java.util.Set[String], java.util.List[String]] =
      grouping.map { case (k, v) => (k.asJava: java.util.Set[String], v.asJava: java.util.List[String]) }.asJava

    val javaLabelledSources: java.util.List[LabelledSource] =
      labelledSources.toList.map(x => new LabelledSource(x._2, x._1.id)).asJava

    new GroupingResult(javaGrouping, javaLabelledSources)
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
