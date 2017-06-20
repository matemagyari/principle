package org.tindalos.principle.domain.agents.structure

import scala.collection.immutable.Seq

object Graph {

  type NodeId = String

  case class Node(id: NodeId, dependencies: Set[NodeId], dependants: Set[NodeId])

  case class Peninsula(val frontNodes: Set[Node], val subgraph: Set[Node]) {
    assert(frontNodes.subsetOf(subgraph))
    val island = isIsland(subgraph)
  }

  case class SubgraphDecomposition(val peninsulas: Seq[Peninsula])

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

  def isIsland(subgraph: Set[Node]) = {
    val subgraphDependencies = subgraph.flatMap(n => n.dependants ++ n.dependants)
    val externalDependencies = subgraphDependencies &~ subgraph.map(_.id)
    externalDependencies.isEmpty
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

  private def findDetachableSubgraph(n: Node, graph: Set[Node]) = {
    def helper(startNode: Node, upstreamNodes: Set[Node]): Set[Node] = {
      val result = for {
        node <- graph
        if (startNode.dependencies.contains(node.id) //node is a direct dependency of startNode
          && ((node.dependants - startNode.id) &~ upstreamNodes.map(_.id)).isEmpty //node has no upstream deps outside upstreamNodes
          && !upstreamNodes.contains(node)) //and node is not among upstreamNodes
      } yield helper(node, upstreamNodes + startNode)
      (upstreamNodes + startNode) ++ result.flatten
    }
    helper(n, Set())
  }

  def findDetachableSubgraphs(graph: Set[Node]) = {
    val result = for {
      node <- graph
      sg = Graph.findDetachableSubgraph(node, graph)
      if (sg.size > 1)
    } yield (node, sg)

    val x = result
      .groupBy(_._2)

    val peninsulas = result
      .groupBy(_._2) //Map[Set[Node],Set[(Node,Set[Node])]]
      .toList
      .map { kv =>
        val subgraph: Set[Node] = kv._1
        val frontNodes: Set[Node] = kv._2.map(_._1)
        Peninsula(frontNodes, subgraph)
      }
      .sortBy(_.subgraph.size)
      .reverse

    SubgraphDecomposition(peninsulas)
  }

}
