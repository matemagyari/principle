package org.tindalos.principle.domain.detector.structure


object Structure {

  type NodeId = String

  case class Node(id: NodeId, dependencies: Set[NodeId], dependants: Set[NodeId])

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
      (internals.size/2, externals.size)
    }

    val leastBelongingNode = nodes.map(n => (n.id, nodeBelongingness(n))).toList.sortBy(_._2).head

    def cohesion() = generalCohesion //Math.max(generalCohesion, internalCohesion)

    //what ratio a node shares with all the arcs related to the group
    def nodeBelongingness(n:Node) =
      (n.dependants.size + n.dependencies.size).toDouble / (internalArcsNo + externalArcsNo).toDouble

    val cohesion3 = nodes.toList.map(nodeBelongingness).sum / nodes.size

    //cohesion of a component: in [0,1). 0.0 - no cohesion
    //how much the number of directed arcs decreases with grouping the nodes
    val generalCohesion = {

      val arcsNo = internalArcsNo + externalArcsNo

      if (arcsNo == 0) 0.0
      else 1 - externalGroupConnectionsNo.toDouble / arcsNo.toDouble
    }

    //purely internal cohesion
    val internalCohesion = {
      if (nodes.size == 1) 0.0
      else internalArcsNo.toDouble / (nodes.size * (nodes.size - 1)).toDouble
    }
    //doesn't really mean much
    val cohesion2 = internalArcsNo.toDouble / externalArcsNo.toDouble


    lazy val isIsolated = externalDependencies.isEmpty && externalDependants.isEmpty

    def merge(c: NodeGroup) = NodeGroup(nodes ++ c.nodes)

    def commonDependenciesRatio(c: NodeGroup) =
      if (externalDependencies.isEmpty)
        if (c.externalDependencies.isEmpty) 1.0
        else 0.0
      else (externalDependencies & c.externalDependencies).size.toDouble / externalDependencies.size.toDouble

    def commonDependantsRatio(c: NodeGroup) =
      if (externalDependants.isEmpty)
        if (c.externalDependants.isEmpty) 1.0
        else 0.0
      else (externalDependants & c.externalDependants).size.toDouble / externalDependants.size.toDouble

    //how many of classes of 'c' this component depends on
    def connectionRate(c: NodeGroup) = (externalDependencies & c.nodes.map(_.id)).size.toDouble / c.nodes.size.toDouble

    def gravityTo(c: NodeGroup) = {
      if (isIsolated && c.isIsolated)
        0.0
      else if (isConnectedTo(c) || c.isConnectedTo(this))
        commonDependenciesRatio(c) * commonDependantsRatio(c)
      else 0.0
    }

    def gravityBetween(c: NodeGroup) = {
      gravityTo(c) * c.gravityTo(this)
      //val d = (1-g) / (depsCount + c.depsCount)
      //1 - d
    }

    def isConnectedTo(c: NodeGroup) = !((externalDependencies & c.nodes.map(_.id)).isEmpty)

    def cohesionDelta(c: NodeGroup) = {
      val mergedCohesion = merge(c).cohesion()
      val delta1 = mergedCohesion - cohesion()
      val delta2 = mergedCohesion - c.cohesion()
      Math.min(delta1, delta2)
    }
    override def toString() = nodes.foldLeft("")(_ + "," + _.id.toString)
  }

}
