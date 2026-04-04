package org.tindalos.principle.domain.analyzers;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.domain.plan.AnalysisInput;
import org.tindalos.principle.domain.AnalysisRunner;
import org.tindalos.principle.domain.analyzers.sdp.SDPResult;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.PackageCouplingConstraints;
import org.tindalos.principle.domain.constraints.SDP;
import org.tindalos.principle.domain.plan.AnalysisPlan;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

import static org.junit.Assert.assertEquals;

public class SDPTest {

    private AnalysisPlan plan;
    private final AnalysisRunner analysisRunner = PoorMansDIContainer.buildAnalysisRunner();
    private final Constraints checks = Constraints.builder()
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

    private void init(String basePackage) {
        plan = new AnalysisPlan(checks, basePackage);
    }

    private SDPResult run(String basePackage) {
        init(basePackage);
        var packageList = new JDependBasedPackageListBuilder(basePackage).build();
        var packageInputs = packageList.stream().map(p -> (PackageWithMetrics) p).toList();
        var result = analysisRunner.run(new AnalysisInput(packageInputs, Set.of(), plan));
        System.out.println("result: " + result);
        assertEquals(1, result.size());
        return (SDPResult) result.get(0);
    }
}
