package org.tindalos.principle.domain.analyzers;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.domain.constraints.ACD;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.domain.plan.AnalysisPlan;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

import static org.junit.Assert.assertEquals;

public class ACDTest {

    private final Constraints constraints = Constraints.builder()
            .packageCoupling(PackageCouplingConstraints.builder().acd(new ACD()).build())
            .build();;

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

    private double run(String basePackage) {
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = PoorMansDIContainer.buildAnalyzer(basePackage);
        return analyzer.analyze(plan).componentDependenciesResult().get().acd();
    }

}
