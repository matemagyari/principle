package org.tindalos.principle.domain.detector.structure


object Structure {

  type NodeId = String

  case class Node(id: NodeId, dependencies: Set[NodeId], dependants: Set[NodeId])

  case class NodeGroup(nodes: Set[Node]) {

    val externalDependencies = nodes.flatMap(n => n.dependencies) -- nodes.map(_.id)
    val externalDependants = nodes.flatMap(n => n.dependants) -- nodes.map(_.id)
    val externalConnectionsNo = externalDependencies.size + externalDependants.size

    def cohesion() = Math.max(generalCohesion, internalCohesion)

    def connectionNos() = {
      val internalNodeIds: Set[NodeId] = nodes.map(_.id)
      /*
      val outArcs = for {
        n <- nodes
        d <- n.dependencies
      } yield (n.id,d)

      val inArcs = for {
        n <- nodes
        d <- n.dependants
      } yield (d,n.id)

      val arcs = outArcs ++ inArcs

      val (internalArcs, externalArcs) = arcs.partition(a => internalNodeIds.contains(a._1) && internalNodeIds.contains(a._2))

      val result = (internalArcs.size, externalArcs.size)
      */
      //seems to be a much shorter way
      val (internals, externals) = nodes.toList.flatMap(n => n.dependants.toList ++ n.dependencies.toList)
                                                .partition(d => internalNodeIds.contains(d))

      (internals.size/2, externals.size)
    }
    //cohesion of a component: in [0,1). 0.0 - no cohesion
    //how much the number of directed arcs decreases with grouping the nodes
    val generalCohesion = {

      val (internalArcsNo, externalArcsNo) = connectionNos()
      val arcsNo = internalArcsNo + externalArcsNo

      if (arcsNo == 0) 0.0
      else 1 - externalConnectionsNo.toDouble / arcsNo.toDouble
    }

    //purely internal cohesion
    val internalCohesion = {
      if (nodes.size == 1) 0.0
      else {
        val (internalArcsNo, _) = connectionNos()
        internalArcsNo.toDouble / (nodes.size * (nodes.size - 1)).toDouble
      }
    }
    //doesn't really mean much
    lazy val cohesion2 = {
      // can be nullpointer
      val (internalArcsNo, externalArcsNo) = connectionNos()
      internalArcsNo.toDouble / externalArcsNo.toDouble
    }

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

    def cohesionDelta(c: NodeGroup) = merge(c).cohesion() - Math.max(cohesion(), c.cohesion())

    override def toString() = nodes.foldLeft("")(_ + "," + _.id.toString)
  }

}
