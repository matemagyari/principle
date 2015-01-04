package org.tindalos.principle.domain.agents.structure

object Graph {

  type NodeId = String

  case class Node(id: NodeId, dependencies: Set[NodeId], dependants: Set[NodeId])

  //def nodeById(id: NodeId) = graph.find(_.id == id).get
  //def findDirectDownstreamNodes(n: Node, graph: Set[Node]) = n.dependencies.map(nodeById)
  //def findDirectUpstreamNodes(n: Node, graph: Set[Node]) = n.dependants.map(nodeById)

  def isValid(graph: Set[Node]) = {

    def symmetryHold(pair: (Node, Node)) =
      if (pair._1.dependencies.contains(pair._2.id)) //if A->B
        (pair._2.dependants.contains(pair._1.id)) // then B has A as dependant
      else if (pair._1.dependants.contains(pair._2.id)) //if A<-B
        (pair._2.dependencies.contains(pair._1.id)) // then
      else true // no relationship

    val pairs = for {
      n1 <- graph
      n2 <- graph if n1 != n2
    } yield (n1, n2)

    pairs.forall(symmetryHold)
  }

  def findDownstreamNodes(n: Node, graph: Set[Node]) = {

    val nodeMap: Map[NodeId, Node] = graph.groupBy(_.id).map(kv => (kv._1, kv._2.toList.head))

    def helper(node: Node, acc: Set[Node]): Set[Node] = {

      val newAcc = acc + node
      val nextNodes = node.dependencies.flatMap(id => nodeMap.get(id)) -- newAcc

      // recursively call on dependencies
      newAcc ++ nextNodes.flatMap(x => helper(x, newAcc ++ nextNodes))
    }
    helper(n, Set())
  }

  def findSources(graph: Set[Node]) = graph.filter(n => n.dependants.isEmpty)

  //find nodes which removal would cause a detach of a subgraph
  def detachNode(n: Node, graph: Set[Node]) = {

    if (n.dependencies.isEmpty) None
    else {
      val subGraph = findDownstreamNodes(n, graph)
      val upstreamNodes = (subGraph - n).flatMap(_.dependants)

      val intersection = upstreamNodes & subGraph.map(_.id)
      //if all the upstream nodes are inside of the subgraph
      if ((upstreamNodes &~ subGraph.map(_.id)).isEmpty) Some(n, subGraph)
      else None
    }
  }


  private def findDetachableSubgraph(n: Node, graph: Set[Node]) = {
    def helper(n: Node, upstreamNodes: Set[Node]): Set[Node] = {
      val result = for {
        node <- graph
        if (n.dependencies.contains(node.id)
          && ((node.dependants-n.id) &~ upstreamNodes.map(_.id)).isEmpty
          && !upstreamNodes.contains(node))
      } yield helper(node, upstreamNodes + n)
      (upstreamNodes + n) ++ result.flatten
    }
    helper(n,Set())
  }

  def findDetachableSubgraphs(graph:Set[Node]) = {
    val result = for {
      n <- graph
      sg = Graph.findDetachableSubgraph(n,graph)
      if (sg.size>1)
    } yield (n,sg)
    result.toList.sortBy(_._2.size).reverse
  }

}
