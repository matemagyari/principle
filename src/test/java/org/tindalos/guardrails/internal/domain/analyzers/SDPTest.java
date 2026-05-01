package org.tindalos.guardrails.internal.domain.analyzers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.sdp.SDPResult;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.guardrails.internal.domain.constraints.SDP;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;

public class SDPTest {

    private final Constraints constraints = Constraints.builder()
            .packageCoupling(PackageCouplingConstraints.builder().sdp(new SDP(0)).build())
            .build();

    @BeforeEach
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple() {
        var result = run("org.tindalos.guardrailstest.sdp");
        result.violations().forEach(System.out::println);
    }

    private SDPResult run(String basePackage) {
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Guardrails.createAnalyser(basePackage);
        return analyzer.analyze(plan).sdpResult().get();
    }
}
