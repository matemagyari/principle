package org.tindalos.principle.domain.analyzers;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.domain.AnalysisInput;
import org.tindalos.principle.domain.core.AnalysisResult;
import org.tindalos.principle.domain.AnalysisRunner;
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyViolationsResult;
import org.tindalos.principle.domain.constraints.Barrier;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.Layering;
import org.tindalos.principle.domain.constraints.ThirdParty;
import org.tindalos.principle.domain.AnalysisPlan;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ThirdPartyTest {

    private final AnalysisRunner analysisRunner = PoorMansDIContainer.buildAnalysisRunner();

    @Before
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple() {
        var barriers = List.of(new Barrier("app", List.of("org.apache.commons.lang3")));
        var thirdParty = new ThirdParty(barriers, 0);

        var result = (ThirdPartyViolationsResult) run("org.tindalos.principletest.thirdparty.simple", thirdParty);
        var expected = Map.of(
                new PackageReference("org.tindalos.principletest.thirdparty.simple.domain"),
                Set.of(new PackageReference("org.apache.commons.lang3")));
        assertEquals(expected, result.violations());
    }

    @Test
    public void allowBoth() {
        var barriers = List.of(new Barrier("app", List.of("org.apache.commons.lang3", "org.apache.commons.io")));
        var thirdParty = new ThirdParty(barriers, 0);

        var result = (ThirdPartyViolationsResult) run("org.tindalos.principletest.thirdparty.simple2", thirdParty);
        assertTrue(result.violations().isEmpty());
    }

    @Test
    public void allowOneRejectTheOther() {
        var barriers = List.of(new Barrier("app", List.of("org.apache.commons.lang3")));
        var thirdParty = new ThirdParty(barriers, 0);

        var result = (ThirdPartyViolationsResult) run("org.tindalos.principletest.thirdparty.simple2", thirdParty);
        var expected = Map.of(
                new PackageReference("org.tindalos.principletest.thirdparty.simple2.app"),
                Set.of(new PackageReference("org.apache.commons.io")));
        assertEquals(expected, result.violations());
    }

    private AnalysisResult run(String basePackage, ThirdParty thirdParty) {
        var expectations = Constraints.builder().layering(layering()).thirdParty(thirdParty).build();
        var packageList = new JDependBasedPackageListBuilder(basePackage).build();
        var plan = new AnalysisPlan(expectations, basePackage);
        var packageInputs = packageList.stream().map(p -> (PackageWithMetrics) p).toList();
        var result = analysisRunner.run(new AnalysisInput(packageInputs, Set.of(), plan));
        return result.get(1);
    }

    private Layering layering() {
        return new Layering(List.of("infrastructure", "app", "domain"), 0);
    }
}
