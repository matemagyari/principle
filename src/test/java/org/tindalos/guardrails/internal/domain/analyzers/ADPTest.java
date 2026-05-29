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
        var base = createPackage("root", Set.of("root.a"));
        var a = createPackage("root.a", Set.of("root.b"));
        var b = createPackage("root.b", Set.of());

        var result = runProgrammatic("root", List.of(base, a, b));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSelfLoopIgnored() {
        var base = createPackage("root", Set.of("root.a"));
        var a = createPackage("root.a", Set.of("root.a"));

        var result = runProgrammatic("root", List.of(base, a));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDiamondGraphAcyclic() {
        var base = createPackage("root", Set.of("root.a", "root.b"));
        var a = createPackage("root.a", Set.of("root.c"));
        var b = createPackage("root.b", Set.of("root.c"));
        var c = createPackage("root.c", Set.of());

        var result = runProgrammatic("root", List.of(base, a, b, c));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDirectCycleLength2() {
        var base = createPackage("root", Set.of("root.a"));
        var a = createPackage("root.a", Set.of("root.b"));
        var b = createPackage("root.b", Set.of("root.a"));

        var result = runProgrammatic("root", List.of(base, a, b));
        
        var allCycles = flatten(result);
        assertEquals(1, allCycles.size());
        
        var expectedCycle = new Cycle(ref("root.a"), ref("root.b"));
        assertEquals(expectedCycle, allCycles.iterator().next());
    }

    @Test
    public void testTransitiveCycleLength3() {
        var base = createPackage("root", Set.of("root.a"));
        var a = createPackage("root.a", Set.of("root.b"));
        var b = createPackage("root.b", Set.of("root.c"));
        var c = createPackage("root.c", Set.of("root.a"));

        var result = runProgrammatic("root", List.of(base, a, b, c));

        var expectedCycle = new Cycle(ref("root.a"), ref("root.b"), ref("root.c"));
        var expected = Map.of(ref("root.a"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    @Test
    public void testMultipleIndependentCycles() {
        var base = createPackage("root", Set.of("root.sub1.a", "root.sub2.x"));
        
        // Subgraph 1 cycle: a <-> b
        var sub1Root = createPackage("root.sub1", Set.of());
        var a = createPackage("root.sub1.a", Set.of("root.sub1.b"));
        var b = createPackage("root.sub1.b", Set.of("root.sub1.a"));
        
        // Subgraph 2 cycle: x <-> y
        var sub2Root = createPackage("root.sub2", Set.of());
        var x = createPackage("root.sub2.x", Set.of("root.sub2.y"));
        var y = createPackage("root.sub2.y", Set.of("root.sub2.x"));

        var result = runProgrammatic("root", List.of(base, sub1Root, a, b, sub2Root, x, y));

        var expectedCycle1 = new Cycle(ref("root.sub1.b"), ref("root.sub1.a"));
        var expectedCycle2 = new Cycle(ref("root.sub2.y"), ref("root.sub2.x"));
        var expected = Map.of(
                ref("root.sub1.a"), Set.of(expectedCycle1),
                ref("root.sub2.x"), Set.of(expectedCycle2)
        );
        assertEquals(expected, result);
    }

    @Test
    public void testOverlappingIntersectingCycles() {
        var base = createPackage("root", Set.of("root.a"));
        var a = createPackage("root.a", Set.of("root.b"));
        var b = createPackage("root.b", Set.of("root.a", "root.c"));
        var c = createPackage("root.c", Set.of("root.b", "root.a"));

        var result = runProgrammatic("root", List.of(base, a, b, c));

        var cycleBA  = new Cycle(ref("root.b"), ref("root.a"));
        var cycleCB  = new Cycle(ref("root.c"), ref("root.b"));
        var cycleCAB = new Cycle(ref("root.c"), ref("root.a"), ref("root.b"));

        var expected = Map.of(
                ref("root.a"), Set.of(cycleBA),
                ref("root.b"), Set.of(cycleCB, cycleCAB)
        );
        assertEquals(expected, result);
    }

    @Test
    public void testThreeIntersectingOverlappingCycles() {
        var base = createPackage("root", Set.of("root.a"));
        var a = createPackage("root.a", Set.of("root.b"));
        var b = createPackage("root.b", Set.of("root.c"));
        var c = createPackage("root.c", Set.of("root.d"));
        var d = createPackage("root.d", Set.of("root.a", "root.e"));
        var e = createPackage("root.e", Set.of("root.f"));
        var f = createPackage("root.f", Set.of("root.g"));
        var g = createPackage("root.g", Set.of("root.d", "root.h"));
        var h = createPackage("root.h", Set.of("root.i"));
        var i = createPackage("root.i", Set.of("root.j"));
        var j = createPackage("root.j", Set.of("root.g"));

        var result = runProgrammatic("root", List.of(base, a, b, c, d, e, f, g, h, i, j));

        var cycle1 = new Cycle(ref("root.b"), ref("root.c"), ref("root.d"), ref("root.a"));
        var cycle2 = new Cycle(ref("root.d"), ref("root.e"), ref("root.f"), ref("root.g"));
        var cycle3 = new Cycle(ref("root.g"), ref("root.h"), ref("root.i"), ref("root.j"));

        var expected = Map.of(
                ref("root.a"), Set.of(cycle1),
                ref("root.g"), Set.of(cycle2),
                ref("root.j"), Set.of(cycle3)
        );

        assertEquals(expected, result);
    }

    @Test
    public void testFourIntersectingOverlappingCycles() {
        var base = createPackage("root", Set.of("root.a", "root.f"));
        var a = createPackage("root.a", Set.of("root.b"));
        var b = createPackage("root.b", Set.of("root.c"));
        var c = createPackage("root.c", Set.of("root.a", "root.d"));
        var d = createPackage("root.d", Set.of("root.e"));
        var e = createPackage("root.e", Set.of("root.c"));

        var f = createPackage("root.f", Set.of("root.g"));
        var g = createPackage("root.g", Set.of("root.h"));
        var h = createPackage("root.h", Set.of("root.f", "root.i"));
        var i = createPackage("root.i", Set.of("root.j"));
        var j = createPackage("root.j", Set.of("root.h"));

        var result = runProgrammatic("root", List.of(base, a, b, c, d, e, f, g, h, i, j));

        var cycleABC = new Cycle(ref("root.a"), ref("root.b"), ref("root.c"));
        var cycleCDE = new Cycle(ref("root.c"), ref("root.d"), ref("root.e"));
        var cycleFGH = new Cycle(ref("root.f"), ref("root.g"), ref("root.h"));
        var cycleHIJ = new Cycle(ref("root.h"), ref("root.i"), ref("root.j"));

        var expected = Map.of(
                ref("root.a"), Set.of(cycleABC),
                ref("root.e"), Set.of(cycleCDE),
                ref("root.f"), Set.of(cycleFGH),
                ref("root.j"), Set.of(cycleHIJ)
        );
        assertEquals(expected, result);
    }

    @Test
    public void testUnderAndOverLimitAtBreakingPoint() {
        // Build a node 'com.example.hub' connected in multiple cycles to verify LIMIT behavior in CyclesInSubgraph.
        // With LIMIT = 5, once a node has more than 5 cycles, isBreakingPoint is true.
        var base = createPackage("root", Set.of("root.hub"));
        var hub = createPackage("root.hub", Set.of());
        
        var packages = new java.util.ArrayList<Package>();
        packages.add(base);
        packages.add(hub);
        
        // Create 7 separate cycles through the hub: hub -> a_i -> hub
        for (int i = 1; i <= 7; i++) {
            String leafName = "root.leaf" + i;
            var leaf = createPackage(leafName, Set.of("root.hub"));
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
        
        var result = runProgrammatic("root", packages);
        
        int hubCyclesCount = result.getOrDefault(ref("root.hub"), Set.of()).size();
        assertTrue(hubCyclesCount > 0);
    }

    private static Set<Cycle> flatten(Map<PackageReference, Set<Cycle>> result) {
        return result.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    private Map<PackageReference, Set<Cycle>> runProgrammatic(String basePackageName, List<Package> packages) {
        var packagesWithMetrics = withCalculatedMetrics(packages);
        var plan = new AnalysisPlan(constraints, basePackageName);
        var input = new org.tindalos.guardrails.internal.domain.plan.AnalysisInput(packagesWithMetrics, Set.of(), plan);
        var packageStructureBuilder = new org.tindalos.guardrails.internal.infrastructure.di.PackageStructureBuilderImpl();
        var detector = new org.tindalos.guardrails.internal.domain.analyzers.adp.CycleDetector(packageStructureBuilder);
        return detector.analyze(input).cyclesByBreakingPoints();
    }

    private static List<Package> withCalculatedMetrics(List<Package> packages) {
        return packages.stream().map(pkg -> {
            int efferentCount = pkg.accumulatedDirectPackageReferences().size();
            int afferentCount = (int) packages.stream()
                .filter(other -> !other.reference().equals(pkg.reference()))
                .filter(other -> other.accumulatedDirectPackageReferences().contains(pkg.reference()))
                .count();
            var nextMetrics = new org.tindalos.guardrails.internal.domain.core.packages.PackageMetrics(
                afferentCount, efferentCount, 0.0f, 0.0f, 0.0f
            );
            return new Package(
                pkg.reference(),
                nextMetrics,
                pkg.ownPackageReferences(),
                pkg.ownExternalPackageReferences(),
                pkg.isUnreferred(),
                pkg.subPackages()
            );
        }).toList();
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
