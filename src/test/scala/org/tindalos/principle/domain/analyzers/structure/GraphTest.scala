package org.tindalos.principle.domain.analyzers.structure

import org.junit.Assert._
import org.junit.Test
import scala.collection.JavaConverters._

class GraphTest {

  @Test
  def findDownstreamNodesForSimpleAcyclicGraph() {
    //a -> b,c | b -> d
    val (a,b,c,d) = ("a","b","c","d")
    val nodeA = new Node(a, Set(b, c).asJava, Set.empty[String].asJava)
    val nodeB = new Node(b, Set(d).asJava, Set(a).asJava)
    val nodeC = new Node(c, Set.empty[String].asJava, Set(a).asJava)
    val nodeD = new Node(d, Set.empty[String].asJava, Set(b).asJava)

    val graph = Set(nodeA, nodeB, nodeC, nodeD)

    assertTrue(Graph.isValid(graph))

    assertEquals(Set(nodeD), Graph.findDownstreamNodes(nodeD, graph))
    assertEquals(Set(nodeC), Graph.findDownstreamNodes(nodeC, graph))
    assertEquals(Set(nodeB,nodeD), Graph.findDownstreamNodes(nodeB, graph))
    assertEquals(Set(nodeA, nodeB, nodeC, nodeD), Graph.findDownstreamNodes(nodeA, graph))

  }


  @Test
  def findDownstreamNodesForDiamondAcyclicGraph() {
    //a -> b,c | b -> d | c->d
    val (a,b,c,d) = ("a","b","c","d")
    val nodeA = new Node(a, Set(b, c).asJava, Set.empty[String].asJava)
    val nodeB = new Node(b, Set(d).asJava, Set(a).asJava)
    val nodeC = new Node(c, Set(d).asJava, Set(a).asJava)
    val nodeD = new Node(d, Set.empty[String].asJava, Set(b,c).asJava)

    val graph = Set(nodeA, nodeB, nodeC, nodeD)

    assertTrue(Graph.isValid(graph))

    assertEquals(Set(nodeD), Graph.findDownstreamNodes(nodeD, graph))
    assertEquals(Set(nodeC,nodeD), Graph.findDownstreamNodes(nodeC, graph))
    assertEquals(Set(nodeB,nodeD), Graph.findDownstreamNodes(nodeB, graph))
    assertEquals(Set(nodeA, nodeB, nodeC, nodeD), Graph.findDownstreamNodes(nodeA, graph))
  }

  @Test
  def findDownstreamNodesForCyclicTriangeGraph() {
    //a -> b,c | b -> d | c->d
    val (a,b,c) = ("a","b","c")
    val nodeA = new Node(a, Set(b).asJava, Set(c).asJava)
    val nodeB = new Node(b, Set(c).asJava, Set(a).asJava)
    val nodeC = new Node(c, Set(a).asJava, Set(b).asJava)

    val graph = Set(nodeA, nodeB, nodeC)

    assertTrue(Graph.isValid(graph))

    assertEquals(graph, Graph.findDownstreamNodes(nodeA, graph))
    assertEquals(graph, Graph.findDownstreamNodes(nodeB, graph))
    assertEquals(graph, Graph.findDownstreamNodes(nodeC, graph))
  }


  @Test
  def findDetachableSubgraphsInGraphWithIslands() {
    //a -> b,c | b -> d | c->d
    val (a,b,c,d,e) = ("a","b","c","d","e")
    val nodeA = new Node(a, Set(b).asJava, Set(c).asJava)
    val nodeB = new Node(b, Set(c).asJava, Set(a).asJava)
    val nodeC = new Node(c, Set(a).asJava, Set(b).asJava)

    val nodeD = new Node(d, Set(e).asJava, Set(e).asJava)
    val nodeE = new Node(e, Set(d).asJava, Set(d).asJava)

    val island1 = Set(nodeA, nodeB, nodeC)
    val island2 = Set(nodeD, nodeE)
    val graph = island1 ++ island2

    assertTrue(Graph.isValid(graph))

    val x = Graph.findDetachableSubgraphs(graph)
    //assertEquals(island1, Graph.findDetachableSubgraphs(graph))
    //assertEquals(island1, Graph.findDownstreamNodes(nodeB, graph))
    //assertEquals(island1, Graph.findDownstreamNodes(nodeC, graph))
  }

  @Test
  def isIslandPositive() {

    val (a,b,c,d,e) = ("a","b","c","d","e")
    val nodeA = new Node(a, Set(b).asJava, Set(c).asJava)
    val nodeB = new Node(b, Set(c).asJava, Set(a).asJava)
    val nodeC = new Node(c, Set(a).asJava, Set(b).asJava)
    val nodeD = new Node(c, Set(a).asJava, Set(e).asJava)
    val nodeE = new Node(c, Set(a).asJava, Set(b).asJava)

    val island = Set(nodeA, nodeB, nodeC)
    val notIsland = Set(nodeA, nodeB, nodeC, nodeD)

    assertTrue(Graph.isIsland(island))
    assertFalse(Graph.isIsland(notIsland))
  }


  @Test
  def findDownstreamNodesForCompleteGraph() {

    val (a,b,c,d) = ("a","b","c","d")
    val nodeA = new Node(a, Set(b,c,d).asJava, Set(b,c,d).asJava)
    val nodeB = new Node(b, Set(a,c,d).asJava, Set(a,c,d).asJava)
    val nodeC = new Node(c, Set(a,b,d).asJava, Set(a,b,d).asJava)
    val nodeD = new Node(d, Set(a,b,c).asJava, Set(a,b,c).asJava)

    val graph = Set(nodeA, nodeB, nodeC, nodeD)

    assertTrue(Graph.isValid(graph))

    assertEquals(graph, Graph.findDownstreamNodes(nodeA, graph))
    assertEquals(graph, Graph.findDownstreamNodes(nodeB, graph))
    assertEquals(graph, Graph.findDownstreamNodes(nodeC, graph))
    assertEquals(graph, Graph.findDownstreamNodes(nodeD, graph))
  }

  @Test
  def simpleGraphValid() {
    val (a,b) = ("a","b") // a->b
    val nodeA = new Node(a, Set.empty[String].asJava, Set(b).asJava)
    val nodeB = new Node(b, Set(a).asJava, Set.empty[String].asJava)

    assertTrue(Graph.isValid(Set(nodeA, nodeB)))
  }
  @Test
  def simpleGraphInValid() {
    val (a,b) = ("a","b") // a->b
    val nodeA = new Node(a, Set.empty[String].asJava, Set(b).asJava)
    val nodeB = new Node(b, Set.empty[String].asJava, Set.empty[String].asJava) // missing reference to A

    assertFalse(Graph.isValid(Set(nodeA, nodeB)))
  }

}
