package org.tindalos.guardrails.internal.domain.analyzers;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.guardrails.internal.domain.constraints.ACD;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;

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
        assertEquals(1.0, run("org.tindalos.guardrailstest.acd.simple1"), 0.01);
    }

    @Test
    public void simple11() {
        assertEquals(1.5, run("org.tindalos.guardrailstest.acd.simple11"), 0.01);
    }

    @Test
    public void simple() {
        assertEquals(2.5, run("org.tindalos.guardrailstest.acd.simple"), 0.01);
    }

    @Test
    public void cyclic3() {
        assertEquals(3.0, run("org.tindalos.guardrailstest.acd.cyclic3"), 0.01);
    }

    @Test
    public void cyclic6() {
        assertEquals(4.33, run("org.tindalos.guardrailstest.acd.cycle6"), 0.01);
    }

    @Test
    public void cyclic62() {
        assertEquals(2.0, run("org.tindalos.guardrailstest.acd.cycle6_2"), 0.01);
    }

    private double run(String basePackage) {
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Guardrails.createAnalyser(basePackage);
        return analyzer.analyze(plan).componentDependenciesResult().get().acd();
    }

}
