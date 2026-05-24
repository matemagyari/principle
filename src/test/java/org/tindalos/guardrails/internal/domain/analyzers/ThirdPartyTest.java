package org.tindalos.guardrails.internal.domain.analyzers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.thirdparty.ThirdPartyViolationsResult;
import org.tindalos.guardrails.internal.domain.constraints.Barrier;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.ThirdParty;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceDefinition;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceGroup;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceId;
import org.tindalos.guardrails.internal.domain.constraints.slices.Slices;
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
        var barriers = List.of(new Barrier("layers.app", List.of("org.apache.commons.lang3")));
        var thirdParty = new ThirdParty(barriers, 0);

        var result = run("org.tindalos.guardrailstest.thirdparty.simple", thirdParty);
        var expected = Map.of(
                new PackageReference("org.tindalos.guardrailstest.thirdparty.simple.domain"),
                Set.of(new PackageReference("org.apache.commons.lang3")));
        assertEquals(expected, result.violations());
    }

    @Test
    public void allowBoth() {
        var barriers = List.of(new Barrier("layers.app", List.of("org.apache.commons.lang3", "org.apache.commons.io")));
        var thirdParty = new ThirdParty(barriers, 0);

        var result = run("org.tindalos.guardrailstest.thirdparty.simple2", thirdParty);
        assertTrue(result.violations().isEmpty());
    }

    @Test
    public void allowOneRejectTheOther() {
        var barriers = List.of(new Barrier("layers.app", List.of("org.apache.commons.lang3")));
        var thirdParty = new ThirdParty(barriers, 0);

        var result = run("org.tindalos.guardrailstest.thirdparty.simple2", thirdParty);
        var expected = Map.of(
                new PackageReference("org.tindalos.guardrailstest.thirdparty.simple2.app"),
                Set.of(new PackageReference("org.apache.commons.io")));
        assertEquals(expected, result.violations());
    }

    private ThirdPartyViolationsResult run(String basePackage, ThirdParty thirdParty) {
        var constraints = Constraints.builder().slices(slices(basePackage)).thirdParty(thirdParty).build();
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Guardrails.createAnalyser(basePackage);
        return analyzer.analyze(plan).thirdPartyViolationsResult().get();
    }

    private Slices slices(String basePackage) {
        Map<SliceId, SliceDefinition> slicesMap = new java.util.LinkedHashMap<>();
        
        SliceId infraId = new SliceId("infrastructure");
        SliceId appId = new SliceId("app");
        SliceId domainId = new SliceId("domain");
        
        slicesMap.put(infraId, new SliceDefinition(infraId, Set.of(new PackageReference(basePackage + ".infrastructure")), Set.of(appId)));
        slicesMap.put(appId, new SliceDefinition(appId, Set.of(new PackageReference(basePackage + ".app")), Set.of(domainId)));
        slicesMap.put(domainId, new SliceDefinition(domainId, Set.of(new PackageReference(basePackage + ".domain")), Set.of()));
        
        return new Slices(List.of(new SliceGroup("layers", slicesMap, 0)));
    }
}