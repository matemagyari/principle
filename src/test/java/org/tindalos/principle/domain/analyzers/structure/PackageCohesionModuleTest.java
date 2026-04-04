package org.tindalos.principle.domain.analyzers.structure;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.tindalos.principle.domain.core.Node;

/**
 * Unit tests for PackageCohesionModule package grouping helpers.
 */
public class PackageCohesionModuleTest {

    @Test
    public void packageOf_returnsOwningPackageName() {
        assertEquals("com.example.domain", PackageCohesionModule.packageOf("com.example.domain.Service"));
    }

    @Test
    public void nodesInPackage_filtersOnlyExactPackage() {
        Set<Node> nodes = Set.of(
                node("com.example.a.Class1"),
                node("com.example.a.sub.Class2"),
                node("com.example.b.Class3"));

        Set<Node> result = PackageCohesionModule.nodesInPackage(nodes, "com.example.a");

        assertEquals(Set.of(node("com.example.a.Class1")), result);
    }

    @Test
    public void getPackageNames_returnsAncestorsUntilRootExclusive() {
        Set<String> names = PackageCohesionModule.getPackageNames("com.example", "com.example.a.sub");

        assertEquals(Set.of("com.example.a", "com.example.a.sub"), names);
    }

    @Test
    public void groupByPackages_andPackageCohesions_useRecursiveGroupingAndSkipSingletons() {
        Set<Node> nodes = Set.of(
                node("com.example.a.Class1"),
                node("com.example.a.sub.Class2"),
                node("com.example.b.Class3"));

        Map<String, Set<Node>> grouped = PackageCohesionModule.groupByPackages("com.example", nodes).stream()
                .collect(java.util.stream.Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        assertEquals(Set.of("com.example.a", "com.example.a.sub", "com.example.b"), grouped.keySet());
        assertEquals(Set.of(node("com.example.a.Class1"), node("com.example.a.sub.Class2")), grouped.get("com.example.a"));

        var cohesions = PackageCohesionModule.packageCohesions("com.example", nodes);
        assertEquals(1, cohesions.size());
        assertEquals("com.example.a", cohesions.get(0).packageName());
        assertTrue(cohesions.get(0).component().nodes().size() > 1);
    }

    private static Node node(String id) {
        return new Node(id, Set.of(), Set.of());
    }
}
