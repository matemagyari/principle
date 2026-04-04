package org.tindalos.principle.domain.analyzers;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.domain.plan.AnalysisInput;
import org.tindalos.principle.domain.AnalysisRunner;
import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesResult;
import org.tindalos.principle.domain.constraints.ACD;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.domain.plan.AnalysisPlan;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;
import org.tindalos.principle.infrastructure.service.jdepend.JDependBasedPackageListBuilder;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

import static org.junit.Assert.assertEquals;

public class ACDTest {

    private AnalysisPlan plan;
    private final AnalysisRunner analysisRunner = PoorMansDIContainer.buildAnalysisRunner();
    private final Constraints expectations = prepareConstraints();

    @Before
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple1() {
        assertEquals(1.0, run("org.tindalos.principletest.acd.simple1"), 0.01);
    }

    @Test
    public void simple11() {
        assertEquals(1.5, run("org.tindalos.principletest.acd.simple11"), 0.01);
    }

    @Test
    public void simple() {
        assertEquals(2.5, run("org.tindalos.principletest.acd.simple"), 0.01);
    }

    @Test
    public void cyclic3() {
        assertEquals(3.0, run("org.tindalos.principletest.acd.cyclic3"), 0.01);
    }

    @Test
    public void cyclic6() {
        assertEquals(4.33, run("org.tindalos.principletest.acd.cycle6"), 0.01);
    }

    @Test
    public void cyclic62() {
        assertEquals(2.0, run("org.tindalos.principletest.acd.cycle6_2"), 0.01);
    }

    private void init(String basePackage) {
        plan = new AnalysisPlan(expectations, basePackage);
    }

    private double run(String basePackage) {
        init(basePackage);
        var packageList = new JDependBasedPackageListBuilder(basePackage).build();
        var packageInputs = packageList.stream()
                .map(p -> (PackageWithMetrics) p)
                .toList();

        var result = analysisRunner.run(new AnalysisInput(packageInputs, Set.of(), plan));
        assertEquals(1, result.size());
        return ((ComponentDependenciesResult) result.get(0)).acd();
    }

    private static Constraints prepareConstraints() {
        return Constraints.builder()
                .packageCoupling(PackageCouplingConstraints.builder().acd(new ACD()).build())
                .build();
    }
}
