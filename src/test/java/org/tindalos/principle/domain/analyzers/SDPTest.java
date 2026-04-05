package org.tindalos.principle.domain.analyzers;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.domain.analyzers.sdp.SDPResult;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.domain.constraints.SDP;
import org.tindalos.principle.domain.plan.AnalysisPlan;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

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
        var analyzer = PoorMansDIContainer.buildAnalyzer(basePackage);
        return analyzer.analyze(plan).sdpResult().get();
    }
}
