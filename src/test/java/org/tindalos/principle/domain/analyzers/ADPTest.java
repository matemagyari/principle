package org.tindalos.principle.domain.analyzers;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.domain.AnalysisInput;
import org.tindalos.principle.domain.AnalysisRunner;
import org.tindalos.principle.domain.analyzers.adp.ADPResult;
import org.tindalos.principle.domain.constraints.ADP;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.domain.AnalysisPlan;
import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner;

import static org.junit.Assert.assertEquals;

public class ADPTest {

    private AnalysisPlan plan;
    private final AnalysisRunner analysisRunner = PoorMansDIContainer.buildAnalysisRunner();
    private final Constraints checks = Constraints.builder()
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

    private void init(String basePackage) {
        plan = new AnalysisPlan(checks, basePackage);
    }

    private Map<PackageReference, Set<Cycle>> run(String basePackage) {
        init(basePackage);
        var packageList = new JDependBasedPackageListBuilder(basePackage).build();
        var classes = MyJDependRunner.createNodesOfClasses(basePackage);
        var packageInputs = packageList.stream().map(p -> (PackageWithMetrics) p).toList();

        var result = analysisRunner.run(new AnalysisInput(packageInputs, classes, plan));
        assertEquals(1, result.size());
        return ((ADPResult) result.get(0)).cyclesByBreakingPoints();
    }

    private static PackageReference ref(String reference) {
        return new PackageReference(reference);
    }
}
