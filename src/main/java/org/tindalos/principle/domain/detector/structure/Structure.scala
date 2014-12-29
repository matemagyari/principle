package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.detector.structure.Graph.{NodeId, Node}


object Structure {

  case class NodeGroup(nodes: Set[Node]) {

    val externalDependencies = nodes.flatMap(n => n.dependencies) -- nodes.map(_.id)
    val externalDependants = nodes.flatMap(n => n.dependants) -- nodes.map(_.id)
    val externalGroupConnectionsNo = externalDependencies.size + externalDependants.size

    //based on nodes not the group
    val (internalArcsNo, externalArcsNo) = {
      val internalNodeIds: Set[NodeId] = nodes.map(_.id)
      val (internals, externals) = nodes.toList.flatMap(n => n.dependants.toList ++ n.dependencies.toList)
        .partition(d => internalNodeIds.contains(d))
      //internal arcs counted twice
      (internals.size / 2, externals.size)
    }

    val leastBelongingNode = nodes.map(n => (n.id, nodeBelongingness(n))).toList.sortBy(_._2).head

    def cohesion() = generalCohesion //Math.max(generalCohesion, internalCohesion)

    //what ratio a node shares with all the arcs related to the group
    def nodeBelongingness(n: Node) =
      (n.dependants.size + n.dependencies.size).toDouble / (internalArcsNo + externalArcsNo).toDouble

    //cohesion of a component: in [0,1). 0.0 - no cohesion
    //how much the number of directed arcs decreases with grouping the nodes
    val generalCohesion = {

      val arcsNo = internalArcsNo + externalArcsNo

      if (arcsNo == 0) 0.0
      else 1 - externalGroupConnectionsNo.toDouble / arcsNo.toDouble
    }

    override def toString() = nodes.foldLeft("")(_ + "," + _.id.toString)
  }

  def merge(n1: NodeGroup, n2: NodeGroup) = NodeGroup(n1.nodes ++ n2.nodes)

  def cohesionDelta(n1: NodeGroup, n2: NodeGroup) = {
    val mergedCohesion = merge(n1, n2).cohesion()
    //if (mergedCohesion < 0.3) 0.0
    //else
    Math.min(mergedCohesion - n1.cohesion(), mergedCohesion - n2.cohesion())
  }

  def commonDependenciesRatio(n1: NodeGroup, n2: NodeGroup) =
    if (n1.externalDependencies.isEmpty)
      if (n2.externalDependencies.isEmpty) 1.0
      else 0.0
    else (n1.externalDependencies & n2.externalDependencies).size.toDouble / n1.externalDependencies.size.toDouble

  def commonDependantsRatio(n1: NodeGroup, n2: NodeGroup) =
    if (n1.externalDependants.isEmpty)
      if (n2.externalDependants.isEmpty) 1.0
      else 0.0
    else (n1.externalDependants & n2.externalDependants).size.toDouble / n1.externalDependants.size.toDouble

  //how many of classes of 'c' this component depends on
  def connectionRate(n1: NodeGroup, n2: NodeGroup) =
    (n1.externalDependencies & n2.nodes.map(_.id)).size.toDouble / n2.nodes.size.toDouble

  def isIsolated(n: NodeGroup) = n.externalDependencies.isEmpty && n.externalDependants.isEmpty

  def gravityTo(n1: NodeGroup, n2: NodeGroup) = {
    if (isIsolated(n1) && isIsolated(n2))
      0.0
    else if (isConnectedTo(n1, n2) || isConnectedTo(n2, n1))
      commonDependenciesRatio(n1, n2) * commonDependantsRatio(n1, n2)
    else 0.0
  }

  def gravityBetween(n1: NodeGroup, n2: NodeGroup) = {
    gravityTo(n1, n2) * gravityTo(n2, n1)
    //val d = (1-g) / (depsCount + c.depsCount)
    //1 - d
  }

  def isConnectedTo(n1: NodeGroup, n2: NodeGroup) = !((n1.externalDependencies & n2.nodes.map(_.id)).isEmpty)

  /*
//purely internal cohesion
lazy val internalCohesion = {
  if (nodes.size == 1) 0.0
  else internalArcsNo.toDouble / (nodes.size * (nodes.size - 1)).toDouble
}
//doesn't really mean much
lazy val cohesion2 = internalArcsNo.toDouble / externalArcsNo.toDouble
lazy val cohesion3 = nodes.toList.map(nodeBelongingness).sum / nodes.size
*/

}
