package org.tindalos.principle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.principle.domain.analyzers.structure.CohesiveGroupsDiscoveryModule;
import org.tindalos.principle.domain.analyzers.structure.Graph;
import org.tindalos.principle.domain.core.Node;
import org.tindalos.principle.domain.analyzers.structure.NodeGroup;
import org.tindalos.principle.domain.analyzers.structure.PackageCohesionModule;
import org.tindalos.principle.domain.analyzers.structure.PackageStructureHints1Finder;
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner;

public final class StructureTestManual {

    private StructureTestManual() {
    }

    public static void main(String[] args) {
        String targetDir = "//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-critical-core/target/classes/";
        String rootPackage = "gamesys.poker.engine.model";

        Set<Node> classes = MyJDependRunner.createNodesOfClasses(rootPackage, targetDir);

        Set<Map.Entry<String, NodeGroup>> packages = PackageCohesionModule.componentsFromPackages(rootPackage, classes);

        var sortedPackages = packages.stream()
                .filter(entry -> entry.getValue().nodes().size() > 1)
                .sorted(Comparator.comparingDouble(entry -> entry.getValue().cohesion()))
                .toList();

        var grouping = PackageStructureHints1Finder.makeGroups(classes);

        System.out.println("\nSources:");
        grouping.labelledSources().stream()
                .sorted(Comparator.comparing(source -> source.label()))
                .forEach(source -> System.out.println(source.label() + " -> " + source.nodeId()));

        System.out.println("\nGroups:");
        grouping.grouping().forEach((key, value) -> {
            System.out.println(key.stream().reduce("Sources: ", (acc, item) -> acc + "," + item));
            value.stream().sorted().forEach(nodeId -> System.out.println("\t" + nodeId));
        });

        long start = System.currentTimeMillis();

        Map<String, Node> classesById = classes.stream().collect(Collectors.toMap(Node::id, node -> node));

        var cohesionsForGrouping = grouping.grouping().entrySet().stream()
                .map(entry -> {
                    Set<Node> nodeSet = entry.getValue().stream().map(classesById::get).collect(Collectors.toSet());
                    return Map.entry(entry.getKey(), new NodeGroup(nodeSet).cohesion());
                })
                .filter(entry -> entry.getKey().size() > 1)
                .toList();

        cohesionsForGrouping.stream()
                .sorted(Comparator.comparingDouble((Map.Entry<Set<String>, Double> entry) -> entry.getValue()).reversed())
                .forEach(entry -> System.out.println(entry.getValue() + " " + entry.getKey()));

        var parts = Graph.findDetachableSubgraphs(MyJDependRunner.createNodesOfClasses("org.tindalos.principle.infrastructure"));
        parts.peninsulas().forEach(peninsula -> {
            System.out.println("Top: " + peninsula.frontNodes() + " " + new NodeGroup(peninsula.subgraph()).cohesion());
            peninsula.subgraph().stream().map(Node::id).sorted().forEach(nodeId -> System.out.println("\t" + nodeId));
        });

        Set<NodeGroup> initialComponents = classes.stream()
                .map(node -> new NodeGroup(Collections.singleton(node)))
                .collect(Collectors.toSet());

        start = System.currentTimeMillis();
        var components = new ArrayList<>(CohesiveGroupsDiscoveryModule.collapseToLimit(initialComponents));
        components.sort(Comparator.comparingInt((NodeGroup group) -> group.nodes().size()).reversed());
        System.out.println("Time1: " + (System.currentTimeMillis() - start));

        var componentsSortedBySize = components.stream()
                .sorted(Comparator.comparingInt((NodeGroup group) -> group.nodes().size()).reversed())
                .toList();
        var componentsSortedByCohesion = components.stream()
                .sorted(Comparator.comparingDouble((NodeGroup group) -> group.cohesion()).reversed())
                .toList();

        System.out.println(sortedPackages.size() + componentsSortedBySize.size() + componentsSortedByCohesion.size());
        System.out.println("end");
    }
}
