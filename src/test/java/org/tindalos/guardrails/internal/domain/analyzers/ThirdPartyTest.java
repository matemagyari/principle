package org.tindalos.guardrails.internal.domain.analyzers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.thirdparty.ThirdPartyViolationsResult;
import org.tindalos.guardrails.internal.domain.constraints.Barrier;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.Layering;
import org.tindalos.guardrails.internal.domain.constraints.ThirdParty;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ThirdPartyTest {

    @BeforeEach
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple() {
        var barriers = List.of(new Barrier("app", List.of("org.apache.commons.lang3")));
        var thirdParty = new ThirdParty(barriers, 0);

        var result = run("org.tindalos.guardrailstest.thirdparty.simple", thirdParty);
        var expected = Map.of(
                new PackageReference("org.tindalos.guardrailstest.thirdparty.simple.domain"),
                Set.of(new PackageReference("org.apache.commons.lang3")));
        assertEquals(expected, result.violations());
    }

    @Test
    public void allowBoth() {
        var barriers = List.of(new Barrier("app", List.of("org.apache.commons.lang3", "org.apache.commons.io")));
        var thirdParty = new ThirdParty(barriers, 0);

        var result = run("org.tindalos.guardrailstest.thirdparty.simple2", thirdParty);
        assertTrue(result.violations().isEmpty());
    }

    @Test
    public void allowOneRejectTheOther() {
        var barriers = List.of(new Barrier("app", List.of("org.apache.commons.lang3")));
        var thirdParty = new ThirdParty(barriers, 0);

        var result = run("org.tindalos.guardrailstest.thirdparty.simple2", thirdParty);
        var expected = Map.of(
                new PackageReference("org.tindalos.guardrailstest.thirdparty.simple2.app"),
                Set.of(new PackageReference("org.apache.commons.io")));
        assertEquals(expected, result.violations());
    }

    private ThirdPartyViolationsResult run(String basePackage, ThirdParty thirdParty) {
        var constraints = Constraints.builder().layering(layering()).thirdParty(thirdParty).build();
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Guardrails.createAnalyser(basePackage);
        return analyzer.analyze(plan).thirdPartyViolationsResult().get();
    }

    private Layering layering() {
        return new Layering(List.of("infrastructure", "app", "domain"), 0);
    }
}
