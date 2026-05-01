package org.tindalos.guardrails.internal.domain.analyzers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.submodulesblueprint.Submodule;
import org.tindalos.guardrails.internal.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleId;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.constraints.YAMLBasedSubmodulesBlueprintProvider;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlueprintTest {

    @BeforeEach
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void missingAndIllegal() {
        var result = run("org.tindalos.guardrailstest.submodulesblueprint", "src/test/resources/guardrails_blueprint_test.yaml");

        var mod1 = fakeSubmodule("MOD1");
        var mod2 = fakeSubmodule("MOD2");
        var mod3 = fakeSubmodule("MOD3");

        assertEquals(Map.of(mod3, Set.of(mod2)), result.illegalDependencies());
        assertEquals(Map.of(mod1, Set.of(mod2)), result.missingDependencies());
    }

    @Test
    public void overlapping() {
        var result = run("org.tindalos.guardrailstest.submodulesblueprint", "src/test/resources/guardrails_blueprint_test_overlapping.yaml");

        assertEquals(Map.of(), result.illegalDependencies());
        assertEquals(Map.of(), result.missingDependencies());
        assertTrue(!result.overlaps().isEmpty(), "Expected overlaps to be detected");
    }

    @Test
    public void violationsCount() {
        var result = run("org.tindalos.guardrailstest.submodulesblueprint", "src/test/resources/guardrails_blueprint_test.yaml");

        assertEquals(2, result.violationsNumber());
        assertEquals(1, result.illegalDependencies().size());
        assertEquals(1, result.missingDependencies().size());
    }

    @Test
    public void illegalDependenciesOnly() {
        var result = run("org.tindalos.guardrailstest.submodulesblueprint", "src/test/resources/guardrails_blueprint_test.yaml");

        var mod3 = fakeSubmodule("MOD3");
        var mod2 = fakeSubmodule("MOD2");

        assertTrue(result.illegalDependencies().containsKey(mod3), "MOD3 should have illegal dependencies");
        var illegalDeps = result.illegalDependencies().get(mod3);
        assertTrue(illegalDeps.contains(mod2), "MOD3 illegally depends on MOD2");
    }

    @Test
    public void missingDependenciesOnly() {
        var result = run("org.tindalos.guardrailstest.submodulesblueprint", "src/test/resources/guardrails_blueprint_test.yaml");

        var mod1 = fakeSubmodule("MOD1");
        var mod2 = fakeSubmodule("MOD2");

        assertTrue(result.missingDependencies().containsKey(mod1), "MOD1 should have missing dependencies");
        var missingDeps = result.missingDependencies().get(mod1);
        assertTrue(missingDeps.contains(mod2), "MOD1 is missing dependency on MOD2");
    }

    @Test
    public void constraintsFailedWhenViolationsExceedThreshold() {
        var result = run("org.tindalos.guardrailstest.submodulesblueprint", "src/test/resources/guardrails_blueprint_test.yaml");
        assertTrue(result.constraintViolated(), "Expectations should fail when violations exceed threshold");
    }

    @Test
    public void verifySubmoduleEquality() {
        var mod1a = fakeSubmodule("MOD1");
        var mod1b = fakeSubmodule("MOD1");
        var mod2 = fakeSubmodule("MOD2");

        assertEquals(mod1a, mod1b);
        assertTrue(!mod1a.equals(mod2), "Submodules with different IDs should not be equal");
    }

    @Test
    public void verifyResultStructure() {
        var result = run("org.tindalos.guardrailstest.submodulesblueprint", "src/test/resources/guardrails_blueprint_test.yaml");

        assertEquals(0, result.threshold());
        assertTrue(result.illegalDependencies() instanceof java.util.Map<?, ?>, "Illegal dependencies should be a Map");
        assertTrue(result.missingDependencies() instanceof java.util.Map<?, ?>, "Missing dependencies should be a Map");
    }

    @Test
    public void blueprintOkParsingSucceeds() {
        var result = run("org.tindalos.guardrailstest.submodulesblueprint", "src/test/resources/guardrails_blueprint_ok.yaml");

        assertEquals(0, result.threshold());
        assertTrue(result.overlaps().isEmpty(), "Valid blueprint should have no overlaps");
        assertTrue(result.violationsNumber() >= 0, "Violations should be non-negative");
    }

    private Submodule fakeSubmodule(String name) {
        return new Submodule(new SubmoduleId(name), Set.<PackageWithMetrics>of(), Set.<SubmoduleId>of());
    }

    private SubmodulesBlueprintAnalysisResult run(String basePackage, String location) {
        var provider = new YAMLBasedSubmodulesBlueprintProvider();
        var yamlObject = readYamlObject(location);
        yamlObject.putIfAbsent("root_package", basePackage);
        var submoduleDefinitions = provider.readSubmoduleDefinitions(yamlObject);
        var constraints = Constraints.builder().submoduleDefinitions(submoduleDefinitions).build();
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Guardrails.createAnalyser(basePackage);
        return analyzer.analyze(plan).submodulesBlueprintAnalysisResult().get();
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> readYamlObject(String location) {
        try {
            var yaml = Files.readString(Path.of(location));
            return (Map<String, Object>) new Yaml().load(yaml);
        } catch (IOException ex) {
            throw new RuntimeException("Failed to read blueprint file: " + location, ex);
        }
    }
}
