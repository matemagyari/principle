package org.tindalos.principle.domain.analyzers;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.domain.AnalysisInput;
import org.tindalos.principle.domain.AnalysisRunner;
import org.tindalos.principle.domain.analyzers.layering.LayerReference;
import org.tindalos.principle.domain.analyzers.layering.LayerViolationsResult;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.Layering;
import org.tindalos.principle.domain.AnalysisPlan;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

import static org.junit.Assert.assertEquals;

public class LayeringTest {

    private AnalysisPlan plan;
    private final AnalysisRunner analysisRunner = PoorMansDIContainer.buildAnalysisRunner();
    private final Constraints expectations = prepareChecks();

    @Before
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple() {
        var result = run("org.tindalos.principletest.layering.simple");
        var expected = Set.of(
                new LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.app"),
                new LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.infrastructure"),
                new LayerReference("org.tindalos.principletest.layering.simple.app", "org.tindalos.principletest.layering.simple.infrastructure"));
        assertEquals(expected, Set.copyOf(result));
    }

    @Test
    public void deeper() {
        var result = run("org.tindalos.principletest.layering.deeper");
        var expected = Set.of(new LayerReference(
                "org.tindalos.principletest.layering.deeper.domain.aaa",
                "org.tindalos.principletest.layering.deeper.app.bbb.ccc"));
        assertEquals(expected, Set.copyOf(result));
    }

    private void init(String basePackage) {
        plan = new AnalysisPlan(expectations, basePackage);
    }

    private List<LayerReference> run(String basePackage) {
        init(basePackage);
        var packageList = new JDependBasedPackageListBuilder(basePackage).build();
        var packageInputs = packageList.stream().map(p -> (PackageWithMetrics) p).toList();
        var result = analysisRunner.run(new AnalysisInput(packageInputs, Set.of(), plan));
        assertEquals(1, result.size());
        return ((LayerViolationsResult) result.get(0)).violations();
    }

    private static Constraints prepareChecks() {
        return Constraints.builder().layering(new Layering(List.of("infrastructure", "app", "domain"), 0)).build();
    }
}
