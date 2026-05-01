package org.tindalos.guardrails.internal.domain.analyzers.structure;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.tindalos.guardrails.internal.domain.core.Node;

public class GraphTest {

    @Test
    public void findDownstreamNodesForSimpleAcyclicGraph() {
        var nodeA = new Node("a", Set.of("b", "c"), Set.of());
        var nodeB = new Node("b", Set.of("d"), Set.of("a"));
        var nodeC = new Node("c", Set.of(), Set.of("a"));
        var nodeD = new Node("d", Set.of(), Set.of("b"));

        var graph = Set.of(nodeA, nodeB, nodeC, nodeD);

        assertTrue(Graph.isValid(graph));
        assertEquals(Set.of(nodeD), Graph.findDownstreamNodes(nodeD, graph));
        assertEquals(Set.of(nodeC), Graph.findDownstreamNodes(nodeC, graph));
        assertEquals(Set.of(nodeB, nodeD), Graph.findDownstreamNodes(nodeB, graph));
        assertEquals(Set.of(nodeA, nodeB, nodeC, nodeD), Graph.findDownstreamNodes(nodeA, graph));
    }

    @Test
    public void findDownstreamNodesForDiamondAcyclicGraph() {
        var nodeA = new Node("a", Set.of("b", "c"), Set.of());
        var nodeB = new Node("b", Set.of("d"), Set.of("a"));
        var nodeC = new Node("c", Set.of("d"), Set.of("a"));
        var nodeD = new Node("d", Set.of(), Set.of("b", "c"));

        var graph = Set.of(nodeA, nodeB, nodeC, nodeD);

        assertTrue(Graph.isValid(graph));
        assertEquals(Set.of(nodeD), Graph.findDownstreamNodes(nodeD, graph));
        assertEquals(Set.of(nodeC, nodeD), Graph.findDownstreamNodes(nodeC, graph));
        assertEquals(Set.of(nodeB, nodeD), Graph.findDownstreamNodes(nodeB, graph));
        assertEquals(Set.of(nodeA, nodeB, nodeC, nodeD), Graph.findDownstreamNodes(nodeA, graph));
    }

    @Test
    public void findDownstreamNodesForCyclicTriangeGraph() {
        var nodeA = new Node("a", Set.of("b"), Set.of("c"));
        var nodeB = new Node("b", Set.of("c"), Set.of("a"));
        var nodeC = new Node("c", Set.of("a"), Set.of("b"));

        var graph = Set.of(nodeA, nodeB, nodeC);

        assertTrue(Graph.isValid(graph));
        assertEquals(graph, Graph.findDownstreamNodes(nodeA, graph));
        assertEquals(graph, Graph.findDownstreamNodes(nodeB, graph));
        assertEquals(graph, Graph.findDownstreamNodes(nodeC, graph));
    }

    @Test
    public void findDetachableSubgraphsInGraphWithIslands() {
        var nodeA = new Node("a", Set.of("b"), Set.of("c"));
        var nodeB = new Node("b", Set.of("c"), Set.of("a"));
        var nodeC = new Node("c", Set.of("a"), Set.of("b"));
        var nodeD = new Node("d", Set.of("e"), Set.of("e"));
        var nodeE = new Node("e", Set.of("d"), Set.of("d"));

        var island1 = Set.of(nodeA, nodeB, nodeC);
        var island2 = Set.of(nodeD, nodeE);
        var graph = Set.of(nodeA, nodeB, nodeC, nodeD, nodeE);

        assertTrue(Graph.isValid(graph));

        var actual = Graph.findDetachableSubgraphs(graph);

        assertEquals(2, actual.peninsulas().size());
        assertEquals(new Peninsula(island1, island1), actual.peninsulas().get(0));
        assertEquals(new Peninsula(island2, island2), actual.peninsulas().get(1));
    }

    @Test
    public void isIslandPositive() {
        var nodeA = new Node("a", Set.of("b"), Set.of("c"));
        var nodeB = new Node("b", Set.of("c"), Set.of("a"));
        var nodeC = new Node("c", Set.of("a"), Set.of("b"));
        var nodeD = new Node("c", Set.of("a"), Set.of("e"));

        var island = Set.of(nodeA, nodeB, nodeC);
        var notIsland = Set.of(nodeA, nodeB, nodeC, nodeD);

        assertTrue(Graph.isIsland(island));
        assertFalse(Graph.isIsland(notIsland));
    }

    @Test
    public void findDownstreamNodesForCompleteGraph() {
        var nodeA = new Node("a", Set.of("b", "c", "d"), Set.of("b", "c", "d"));
        var nodeB = new Node("b", Set.of("a", "c", "d"), Set.of("a", "c", "d"));
        var nodeC = new Node("c", Set.of("a", "b", "d"), Set.of("a", "b", "d"));
        var nodeD = new Node("d", Set.of("a", "b", "c"), Set.of("a", "b", "c"));

        var graph = Set.of(nodeA, nodeB, nodeC, nodeD);

        assertTrue(Graph.isValid(graph));
        assertEquals(graph, Graph.findDownstreamNodes(nodeA, graph));
        assertEquals(graph, Graph.findDownstreamNodes(nodeB, graph));
        assertEquals(graph, Graph.findDownstreamNodes(nodeC, graph));
        assertEquals(graph, Graph.findDownstreamNodes(nodeD, graph));
    }

    @Test
    public void simpleGraphValid() {
        var nodeA = new Node("a", Set.of(), Set.of("b"));
        var nodeB = new Node("b", Set.of("a"), Set.of());

        assertTrue(Graph.isValid(Set.of(nodeA, nodeB)));
    }

    @Test
    public void simpleGraphInValid() {
        var nodeA = new Node("a", Set.of(), Set.of("b"));
        var nodeB = new Node("b", Set.of(), Set.of());

        assertFalse(Graph.isValid(Set.of(nodeA, nodeB)));
    }
}