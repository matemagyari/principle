package org.tindalos.principle.domain.analyzers.structure;

import org.tindalos.principle.domain.core.Node;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility operations for grouping classes by package and deriving package cohesion inputs.
 */
public final class PackageCohesionModule {

    private PackageCohesionModule() {
    }

    public static String packageOf(String nodeId) {
        return nodeId.substring(0, nodeId.lastIndexOf('.'));
    }

    public static Set<Node> nodesInPackage(Set<Node> nodes, String packageName) {
        return nodes.stream()
                .filter(node -> packageOf(node.id()).equals(packageName))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<Map.Entry<String, Set<Node>>> groupByPackages(String rootPackage, Set<Node> nodes) {
        var endPackageNames = nodes.stream()
                .map(node -> packageOf(node.id()))
                .collect(Collectors.toSet());

        var packageNames = endPackageNames.stream()
                .flatMap(packageName -> getPackageNames(rootPackage, packageName).stream())
                .collect(Collectors.toSet());

        return packageNames.stream()
                .map(packageName -> Map.entry(packageName, findNodesInPackageRecursively(packageName, nodes)))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<Map.Entry<String, NodeGroup>> componentsFromPackages(String rootPackage, Set<Node> nodes) {
        return groupByPackages(rootPackage, nodes).stream()
                .map(entry -> Map.entry(entry.getKey(), new NodeGroup(entry.getValue())))
                .collect(Collectors.toUnmodifiableSet());
    }

    public static java.util.List<PackageCohesion> packageCohesions(String rootPackage, Set<Node> nodes) {
        return componentsFromPackages(rootPackage, nodes).stream()
                .filter(entry -> entry.getValue().nodes().size() > 1)
                .map(entry -> new PackageCohesion(entry.getKey(), entry.getValue(), entry.getValue().cohesion()))
                .sorted(java.util.Comparator.comparingDouble(PackageCohesion::cohesion).reversed())
                .toList();
    }

    public static Set<String> getPackageNames(String rootPackage, String packageName) {
        var result = new HashSet<String>();
        var current = packageName;

        while (!rootPackage.equals(current)) {
            result.add(current);
            current = current.substring(0, current.lastIndexOf('.'));
        }

        return Set.copyOf(result);
    }

    private static Set<Node> findNodesInPackageRecursively(String packageName, Set<Node> nodes) {
        return nodes.stream()
                .filter(node -> node.id().startsWith(packageName))
                .collect(Collectors.toUnmodifiableSet());
    }

    public record PackageCohesion(String packageName, NodeGroup component, double cohesion) {
    }
}