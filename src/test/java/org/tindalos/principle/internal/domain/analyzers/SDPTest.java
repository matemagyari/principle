package org.tindalos.principle.internal.domain.analyzers;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.internal.domain.analyzers.sdp.SDPResult;
import org.tindalos.principle.internal.domain.constraints.Constraints;
import org.tindalos.principle.internal.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.internal.domain.constraints.SDP;
import org.tindalos.principle.internal.domain.plan.AnalysisPlan;
import org.tindalos.principle.internal.infrastructure.di.Principle;

public class SDPTest {

    private final Constraints constraints = Constraints.builder()
            .packageCoupling(PackageCouplingConstraints.builder().sdp(new SDP(0)).build())
            .build();

    @Before
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple() {
        var result = run("org.tindalos.principletest.sdp");
        result.violations().forEach(System.out::println);
    }

    private SDPResult run(String basePackage) {
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Principle.createAnalyser(basePackage);
        return analyzer.analyze(plan).sdpResult().get();
    }
}
