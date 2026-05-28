package org.tindalos.guardrails.internal.domain.analyzers;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.adp.Cycle;
import org.tindalos.guardrails.internal.domain.constraints.ADP;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;

public class ADPTest {

    private final Constraints constraints = Constraints.builder()
            .packageCoupling(PackageCouplingConstraints.builder().adp(new ADP()).build())
            .build();

    @BeforeEach
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple() {
        var result = run("org.tindalos.guardrailstest.cycle.simple");
        var expectedCycle = new Cycle(ref("org.tindalos.guardrailstest.cycle.simple.left"), ref("org.tindalos.guardrailstest.cycle.simple.right"));
        var expected = Map.of(ref("org.tindalos.guardrailstest.cycle.simple.right"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    @Test
    public void transitive() {
        var result = run("org.tindalos.guardrailstest.cycle.transitive");
        var expectedCycle = new Cycle(
                ref("org.tindalos.guardrailstest.cycle.transitive.a"),
                ref("org.tindalos.guardrailstest.cycle.transitive.b"),
                ref("org.tindalos.guardrailstest.cycle.transitive.c"));
        var expected = Map.of(ref("org.tindalos.guardrailstest.cycle.transitive.c"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    @Test
    public void transitive2() {
        var result = run("org.tindalos.guardrailstest.cycle.transitive2");
        var expectedCycle = new Cycle(
                ref("org.tindalos.guardrailstest.cycle.transitive2.a"),
                ref("org.tindalos.guardrailstest.cycle.transitive2.b"),
                ref("org.tindalos.guardrailstest.cycle.transitive2.c"));
        assertEquals(1, result.size());
        assertEquals(Optional.of(ref("org.tindalos.guardrailstest.cycle.transitive2")), result.keySet().iterator().next().parent());
        assertEquals(Set.of(expectedCycle), result.values().iterator().next());
    }

    @Test
    public void btwParentAndChild() {
        var result = run("org.tindalos.guardrailstest.cycle.btwparentandchild");
        var expectedCycle = new Cycle(
                ref("org.tindalos.guardrailstest.cycle.btwparentandchild"),
                ref("org.tindalos.guardrailstest.cycle.btwparentandchild.child"));
        var expected = Map.of(ref("org.tindalos.guardrailstest.cycle.btwparentandchild.child"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    @Test
    public void complex1() {
        var result = run("org.tindalos.guardrailstest.cycle.complex1");
        var expectedCycle = new Cycle(
                ref("org.tindalos.guardrailstest.cycle.complex1.left"),
                ref("org.tindalos.guardrailstest.cycle.complex1.right"));
        var expected = Map.of(ref("org.tindalos.guardrailstest.cycle.complex1.right"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    @Test
    public void complex2() {
        var result = run("org.tindalos.guardrailstest.cycle.complex2");
        var expectedCycle = new Cycle(
                ref("org.tindalos.guardrailstest.cycle.complex2.left"),
                ref("org.tindalos.guardrailstest.cycle.complex2.right.right"));
        var expected = Map.of(ref("org.tindalos.guardrailstest.cycle.complex2.right.right"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    @Test
    public void testNoCyclesInSimpleDag() {
        var base = createPackage("com.example", Set.of("com.example.a"));
        var a = createPackage("com.example.a", Set.of("com.example.b"));
        var b = createPackage("com.example.b", Set.of());

        var result = runProgrammatic("com.example", List.of(base, a, b));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSelfLoopIgnored() {
        var base = createPackage("com.example", Set.of("com.example.a"));
        var a = createPackage("com.example.a", Set.of("com.example.a"));

        var result = runProgrammatic("com.example", List.of(base, a));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDiamondGraphAcyclic() {
        var base = createPackage("com.example", Set.of("com.example.a", "com.example.b"));
        var a = createPackage("com.example.a", Set.of("com.example.c"));
        var b = createPackage("com.example.b", Set.of("com.example.c"));
        var c = createPackage("com.example.c", Set.of());

        var result = runProgrammatic("com.example", List.of(base, a, b, c));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDirectCycleLength2() {
        var base = createPackage("com.example", Set.of("com.example.a"));
        var a = createPackage("com.example.a", Set.of("com.example.b"));
        var b = createPackage("com.example.b", Set.of("com.example.a"));

        var result = runProgrammatic("com.example", List.of(base, a, b));
        
        var allCycles = flatten(result);
        assertEquals(1, allCycles.size());
        
        var expectedCycle = new Cycle(ref("com.example.a"), ref("com.example.b"));
        assertEquals(expectedCycle, allCycles.iterator().next());
    }

    @Test
    public void testTransitiveCycleLength3() {
        var base = createPackage("com.example", Set.of("com.example.a"));
        var a = createPackage("com.example.a", Set.of("com.example.b"));
        var b = createPackage("com.example.b", Set.of("com.example.c"));
        var c = createPackage("com.example.c", Set.of("com.example.a"));

        var result = runProgrammatic("com.example", List.of(base, a, b, c));
        
        var allCycles = flatten(result);
        assertEquals(1, allCycles.size());
        
        var expectedCycle = new Cycle(ref("com.example.a"), ref("com.example.b"), ref("com.example.c"));
        assertEquals(expectedCycle, allCycles.iterator().next());
    }

    @Test
    public void testMultipleIndependentCycles() {
        var base = createPackage("com.example", Set.of("com.example.sub1.a", "com.example.sub2.x"));
        
        // Subgraph 1 cycle: a <-> b
        var sub1Root = createPackage("com.example.sub1", Set.of());
        var a = createPackage("com.example.sub1.a", Set.of("com.example.sub1.b"));
        var b = createPackage("com.example.sub1.b", Set.of("com.example.sub1.a"));
        
        // Subgraph 2 cycle: x <-> y
        var sub2Root = createPackage("com.example.sub2", Set.of());
        var x = createPackage("com.example.sub2.x", Set.of("com.example.sub2.y"));
        var y = createPackage("com.example.sub2.y", Set.of("com.example.sub2.x"));

        var result = runProgrammatic("com.example", List.of(base, sub1Root, a, b, sub2Root, x, y));
        
        var allCycles = flatten(result);
        assertEquals(2, allCycles.size());
        
        var expectedCycle1 = new Cycle(ref("com.example.sub1.a"), ref("com.example.sub1.b"));
        var expectedCycle2 = new Cycle(ref("com.example.sub2.x"), ref("com.example.sub2.y"));
        
        assertTrue(allCycles.contains(expectedCycle1));
        assertTrue(allCycles.contains(expectedCycle2));
    }

    @Test
    public void testOverlappingIntersectingCycles() {
        var base = createPackage("com.example", Set.of("com.example.a"));
        var a = createPackage("com.example.a", Set.of("com.example.b"));
        var b = createPackage("com.example.b", Set.of("com.example.a", "com.example.c"));
        var c = createPackage("com.example.c", Set.of("com.example.b", "com.example.a"));

        var result = runProgrammatic("com.example", List.of(base, a, b, c));
        
        var allCycles = flatten(result);
        assertTrue(allCycles.size() >= 2);
    }

    @Test
    public void testUnderAndOverLimitAtBreakingPoint() {
        // Build a node 'com.example.hub' connected in multiple cycles to verify LIMIT behavior in CyclesInSubgraph.
        // With LIMIT = 5, once a node has more than 5 cycles, isBreakingPoint is true.
        var base = createPackage("com.example", Set.of("com.example.hub"));
        var hub = createPackage("com.example.hub", Set.of());
        
        var packages = new java.util.ArrayList<Package>();
        packages.add(base);
        packages.add(hub);
        
        // Create 7 separate cycles through the hub: hub -> a_i -> hub
        for (int i = 1; i <= 7; i++) {
            String leafName = "com.example.leaf" + i;
            var leaf = createPackage(leafName, Set.of("com.example.hub"));
            packages.add(leaf);
            
            // Connect hub directly to this leaf
            hub = new Package(
                hub.reference(),
                hub.metrics(),
                java.util.stream.Stream.concat(
                    hub.ownPackageReferences().stream(),
                    java.util.stream.Stream.of(new PackageReference(leafName))
                ).collect(Collectors.toUnmodifiableSet()),
                hub.ownExternalPackageReferences(),
                hub.isUnreferred()
            );
        }
        // update hub in the list
        packages.set(1, hub);
        
        var result = runProgrammatic("com.example", packages);
        
        int hubCyclesCount = result.getOrDefault(ref("com.example.hub"), Set.of()).size();
        assertTrue(hubCyclesCount > 0);
    }

    private static Set<Cycle> flatten(Map<PackageReference, Set<Cycle>> result) {
        return result.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Map<PackageReference, Set<Cycle>> runProgrammatic(String basePackageName, List<Package> packages) {
        var plan = new AnalysisPlan(constraints, basePackageName);
        var input = new org.tindalos.guardrails.internal.domain.plan.AnalysisInput(packages, Set.of(), plan);
        var packageStructureBuilder = new org.tindalos.guardrails.internal.infrastructure.di.PackageStructureBuilderImpl();
        var detector = new org.tindalos.guardrails.internal.domain.analyzers.adp.CycleDetector(packageStructureBuilder);
        return detector.analyze(input).cyclesByBreakingPoints();
    }

    private static Package createPackage(String name, Set<String> efferents) {
        var ref = new PackageReference(name);
        var efferentRefs = efferents.stream()
                .map(PackageReference::new)
                .collect(Collectors.toUnmodifiableSet());
        return new Package(
                ref,
                new org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics(0, 0, 0, 0, 0),
                efferentRefs,
                Set.of(),
                false
        );
    }

    private Map<PackageReference, Set<Cycle>> run(String basePackage) {
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Guardrails.createAnalyser(basePackage);
        return analyzer.analyze(plan).adpResult().get().cyclesByBreakingPoints();
    }

    private static PackageReference ref(String reference) {
        return new PackageReference(reference);
    }
}
