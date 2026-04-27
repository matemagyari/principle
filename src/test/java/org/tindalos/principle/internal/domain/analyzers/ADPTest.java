package org.tindalos.principle.internal.domain.analyzers;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.internal.domain.constraints.ADP;
import org.tindalos.principle.internal.domain.constraints.Constraints;
import org.tindalos.principle.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.internal.domain.core.Cycle;
import org.tindalos.principle.internal.domain.core.packages.PackageReference;
import org.tindalos.principle.internal.domain.plan.AnalysisPlan;
import org.tindalos.principle.internal.infrastructure.di.Principle;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ADPTest {

    private AnalysisPlan plan;
    private final Constraints constraints = Constraints.builder()
            .packageCoupling(PackageCouplingConstraints.builder().adp(new ADP()).build())
            .build();

    @Before
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple() {
        var result = run("org.tindalos.principletest.cycle.simple");
        var expectedCycle = new Cycle(ref("org.tindalos.principletest.cycle.simple.left"), ref("org.tindalos.principletest.cycle.simple.right"));
        var expected = Map.of(ref("org.tindalos.principletest.cycle.simple.right"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    @Test
    public void transitive() {
        var result = run("org.tindalos.principletest.cycle.transitive");
        var expectedCycle = new Cycle(
                ref("org.tindalos.principletest.cycle.transitive.a"),
                ref("org.tindalos.principletest.cycle.transitive.b"),
                ref("org.tindalos.principletest.cycle.transitive.c"));
        var expected = Map.of(ref("org.tindalos.principletest.cycle.transitive.c"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    @Test
    public void transitive2() {
        var result = run("org.tindalos.principletest.cycle.transitive2");
        var expectedCycle = new Cycle(
                ref("org.tindalos.principletest.cycle.transitive2.a"),
                ref("org.tindalos.principletest.cycle.transitive2.b"),
                ref("org.tindalos.principletest.cycle.transitive2.c"));
        assertEquals(1, result.size());
        assertEquals(Optional.of(ref("org.tindalos.principletest.cycle.transitive2")), result.keySet().iterator().next().parent());
        assertEquals(Set.of(expectedCycle), result.values().iterator().next());
    }

    @Test
    public void btwParentAndChild() {
        var result = run("org.tindalos.principletest.cycle.btwparentandchild");
        var expectedCycle = new Cycle(
                ref("org.tindalos.principletest.cycle.btwparentandchild"),
                ref("org.tindalos.principletest.cycle.btwparentandchild.child"));
        var expected = Map.of(ref("org.tindalos.principletest.cycle.btwparentandchild.child"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    @Test
    public void complex1() {
        var result = run("org.tindalos.principletest.cycle.complex1");
        var expectedCycle = new Cycle(
                ref("org.tindalos.principletest.cycle.complex1.left"),
                ref("org.tindalos.principletest.cycle.complex1.right"));
        var expected = Map.of(ref("org.tindalos.principletest.cycle.complex1.right"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    @Test
    public void complex2() {
        var result = run("org.tindalos.principletest.cycle.complex2");
        var expectedCycle = new Cycle(
                ref("org.tindalos.principletest.cycle.complex2.left"),
                ref("org.tindalos.principletest.cycle.complex2.right.right"));
        var expected = Map.of(ref("org.tindalos.principletest.cycle.complex2.right.right"), Set.of(expectedCycle));
        assertEquals(expected, result);
    }

    private Map<PackageReference, Set<Cycle>> run(String basePackage) {
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Principle.createAnalyser(basePackage);
        return analyzer.analyze(plan).adpResult().get().cyclesByBreakingPoints();
    }

    private static PackageReference ref(String reference) {
        return new PackageReference(reference);
    }
}
