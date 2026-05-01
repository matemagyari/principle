package org.tindalos.guardrails.internal.domain.analyzers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.constraints.ADP;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.internal.domain.core.Cycle;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ADPTest {

    private AnalysisPlan plan;
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

    private Map<PackageReference, Set<Cycle>> run(String basePackage) {
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Guardrails.createAnalyser(basePackage);
        return analyzer.analyze(plan).adpResult().get().cyclesByBreakingPoints();
    }

    private static PackageReference ref(String reference) {
        return new PackageReference(reference);
    }
}
