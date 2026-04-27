package org.tindalos.principle.internal.domain.analyzers;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.internal.domain.analyzers.submodulesblueprint.Submodule;
import org.tindalos.principle.internal.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult;
import org.tindalos.principle.internal.domain.constraints.Constraints;
import org.tindalos.principle.internal.domain.constraints.submodules.SubmoduleId;
import org.tindalos.principle.internal.domain.core.packages.PackageWithMetrics;
import org.tindalos.principle.internal.domain.plan.AnalysisPlan;
import org.tindalos.principle.internal.infrastructure.analyzers.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider;
import org.tindalos.principle.internal.infrastructure.di.Principle;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlueprintTest {

    @Before
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void missingAndIllegal() {
        var result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml");

        var mod1 = fakeSubmodule("MOD1");
        var mod2 = fakeSubmodule("MOD2");
        var mod3 = fakeSubmodule("MOD3");

        assertEquals(Map.of(mod3, Set.of(mod2)), result.illegalDependencies());
        assertEquals(Map.of(mod1, Set.of(mod2)), result.missingDependencies());
    }

    @Test
    public void overlapping() {
        var result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test_overlapping.yaml");

        assertEquals(Map.of(), result.illegalDependencies());
        assertEquals(Map.of(), result.missingDependencies());
        assertTrue("Expected overlaps to be detected", !result.overlaps().isEmpty());
    }

    @Test
    public void violationsCount() {
        var result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml");

        assertEquals(2, result.violationsNumber());
        assertEquals(1, result.illegalDependencies().size());
        assertEquals(1, result.missingDependencies().size());
    }

    @Test
    public void illegalDependenciesOnly() {
        var result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml");

        var mod3 = fakeSubmodule("MOD3");
        var mod2 = fakeSubmodule("MOD2");

        assertTrue("MOD3 should have illegal dependencies", result.illegalDependencies().containsKey(mod3));
        var illegalDeps = result.illegalDependencies().get(mod3);
        assertTrue("MOD3 illegally depends on MOD2", illegalDeps.contains(mod2));
    }

    @Test
    public void missingDependenciesOnly() {
        var result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml");

        var mod1 = fakeSubmodule("MOD1");
        var mod2 = fakeSubmodule("MOD2");

        assertTrue("MOD1 should have missing dependencies", result.missingDependencies().containsKey(mod1));
        var missingDeps = result.missingDependencies().get(mod1);
        assertTrue("MOD1 is missing dependency on MOD2", missingDeps.contains(mod2));
    }

    @Test
    public void constraintsFailedWhenViolationsExceedThreshold() {
        var result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml");
        assertTrue("Expectations should fail when violations exceed threshold", result.constraintViolated());
    }

    @Test
    public void verifySubmoduleEquality() {
        var mod1a = fakeSubmodule("MOD1");
        var mod1b = fakeSubmodule("MOD1");
        var mod2 = fakeSubmodule("MOD2");

        assertEquals(mod1a, mod1b);
        assertTrue("Submodules with different IDs should not be equal", !mod1a.equals(mod2));
    }

    @Test
    public void verifyResultStructure() {
        var result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_test.yaml");

        assertEquals(0, result.threshold());
        assertTrue("Illegal dependencies should be a Map", result.illegalDependencies() instanceof java.util.Map<?, ?>);
        assertTrue("Missing dependencies should be a Map", result.missingDependencies() instanceof java.util.Map<?, ?>);
    }

    @Test
    public void blueprintOkParsingSucceeds() {
        var result = run("org.tindalos.principletest.submodulesblueprint", "src/test/resources/principle_blueprint_ok.yaml");

        assertEquals(0, result.threshold());
        assertTrue("Valid blueprint should have no overlaps", result.overlaps().isEmpty());
        assertTrue("Violations should be non-negative", result.violationsNumber() >= 0);
    }

    private Submodule fakeSubmodule(String name) {
        return new Submodule(new SubmoduleId(name), Set.<PackageWithMetrics>of(), Set.<SubmoduleId>of());
    }

    private SubmodulesBlueprintAnalysisResult run(String basePackage, String location) {
        var provider = new YAMLBasedSubmodulesBlueprintProvider();
        var submoduleDefinitions = provider.readSubmoduleDefinitions(basePackage, location, 0);
        var constraints = Constraints.builder().submoduleDefinitions(submoduleDefinitions).build();
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Principle.createAnalyser(basePackage);
        return analyzer.analyze(plan).submodulesBlueprintAnalysisResult().get();
    }
}
