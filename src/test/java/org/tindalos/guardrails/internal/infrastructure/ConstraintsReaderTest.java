package org.tindalos.guardrails.internal.infrastructure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.constraints.exception.InvalidConfigurationException;
import org.tindalos.guardrails.internal.infrastructure.constraints.ConstraintsReader;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ConstraintsReaderTest {

    private File tempFile;

    @AfterEach
    public void cleanup() throws Exception {
        if (tempFile != null && tempFile.exists()) tempFile.delete();
    }

    private String writeTempYaml(String content) throws Exception {
        tempFile = Files.createTempFile("guardrails_test_", ".yml").toFile();
        Files.writeString(tempFile.toPath(), content);
        return tempFile.getAbsolutePath();
    }

    @Test
    public void rootPackage_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                """);

        var plan = ConstraintsReader.readFromFile(Optional.of(path));

        assertEquals("com.example", plan.basePackage());
    }

    @Test
    public void layering_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  layering:
                    layers: [infrastructure, app, domain]
                    violation_threshold: 2
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        var layering = constraints.layering().get();
        assertEquals(List.of("infrastructure", "app", "domain"), layering.layers());
        assertEquals(2, layering.violationThreshold());
    }

    @Test
    public void layering_defaultThresholdIsZero() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  layering:
                    layers: [a, b]
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertEquals(0, constraints.layering().get().violationThreshold());
    }

    @Test
    public void noLayering_layeringIsAbsent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertTrue(constraints.layering().isEmpty());
    }

    @Test
    public void packageCoupling_adpThreshold_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 5
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        var adp = constraints.packageCoupling().get().adp();
        assertTrue(adp.isPresent());
        assertEquals(5, adp.get().violationThreshold());
    }

    @Test
    public void packageCoupling_racdThreshold_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    acd_threshold: 0.35
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        var racd = constraints.packageCoupling().get().racd();
        assertTrue(racd.isPresent());
        assertEquals(0.35, racd.get().threshold(), 0.001);
    }

    @Test
    public void structureAnalysisEnabled_groupingIsPresent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                structure_analysis_enabled: true
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertTrue(constraints.packageCoupling().get().grouping().isPresent());
    }

    @Test
    public void structureAnalysisDisabled_groupingIsAbsent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                structure_analysis_enabled: false
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertFalse(constraints.packageCoupling().get().grouping().isPresent());
    }

    @Test
    public void thirdPartyRestrictions_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                  third_party_restrictions:
                    allowed_libraries:
                      - layer: infrastructure
                        libraries: [org.apache.commons, com.google]
                      - layer: app
                        libraries: [org.apache.commons]
                    violation_threshold: 3
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        var tp = constraints.thirdParty();
        assertTrue(tp.isPresent());
        assertEquals(3, tp.get().violationThreshold());
        assertEquals(2, tp.get().barriers().size());
        assertEquals("infrastructure", tp.get().barriers().get(0).layer());
        assertEquals(List.of("org.apache.commons", "com.google"), tp.get().barriers().get(0).components());
        assertEquals("app", tp.get().barriers().get(1).layer());
        assertEquals(Collections.singletonList("org.apache.commons"), tp.get().barriers().get(1).components());
    }

    @Test
    public void noThirdPartyRestrictions_thirdPartyIsAbsent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertFalse(constraints.thirdParty().isPresent());
    }

    @Test
    public void modules_withThreeModuleDefinitions_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  modules:
                    module-definitions:
                      AUTH: [domain.auth, app.auth]
                      BILLING: [domain.billing, app.billing]
                      NOTIFICATION: [domain.notification, app.notification]
                    module-dependencies:
                      AUTH: [BILLING]
                      BILLING: [NOTIFICATION]
                    violation_threshold: 2
                """);

        var definitions = ConstraintsReader.readFromFile(Optional.of(path)).constraints().submoduleDefinitions().get();

        assertEquals(3, definitions.getDefinitions().size());
        assertEquals(2, definitions.violationThreshold());
    }

    @Test
    public void modules_withDefinitions_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                  modules:
                    module-definitions:
                      MOD1: [domain.mod1]
                    module-dependencies:
                      MOD1: []
                    violation_threshold: 1
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertTrue(constraints.submoduleDefinitions().isPresent());
        assertEquals(1, constraints.submoduleDefinitions().get().violationThreshold());
    }

    @Test
    public void modules_defaultThresholdIsZero() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                  modules:
                    module-definitions:
                      MOD1: [domain.mod1]
                    module-dependencies:
                      MOD1: []
                """);

        var definitions = ConstraintsReader.readFromFile(Optional.of(path)).constraints().submoduleDefinitions().get();

        assertEquals(0, definitions.violationThreshold());
    }

    @Test
    public void noModules_submoduleDefinitionsIsAbsent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertFalse(constraints.submoduleDefinitions().isPresent());
    }

    @Test
    public void modules_withoutDefinitions_submoduleDefinitionsIsAbsent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                  modules:
                    violation_threshold: 1
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertFalse(constraints.submoduleDefinitions().isPresent());
    }

    @Test
    public void missingFile_throwsInvalidConfigurationException() {
      assertThrows(InvalidConfigurationException.class,
          () -> ConstraintsReader.readFromFile(Optional.of("/non/existent/path/guardrails.yml")));
    }

    @Test
    public void fullConfig_isParsedCorrectly() throws Exception {
        var path = writeTempYaml("""
                root_package: org.example.myapp
                constraints:
                  layering:
                    layers: [infrastructure, app, domain]
                    violation_threshold: 0
                  third_party_restrictions:
                    allowed_libraries:
                      - layer: infrastructure
                        libraries: [org.apache.commons]
                    violation_threshold: 0
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                    acd_threshold: 0.5
                  modules:
                    module-definitions:
                      MOD1: [domain.mod1]
                    module-dependencies:
                      MOD1: []
                    violation_threshold: 0
                structure_analysis_enabled: true
                """);

        var plan = ConstraintsReader.readFromFile(Optional.of(path));
        var constraints = plan.constraints();

        assertEquals("org.example.myapp", plan.basePackage());
        assertTrue(constraints.layering().isPresent());
        assertTrue(constraints.thirdParty().isPresent());
        assertTrue(constraints.packageCoupling().isPresent());
        assertTrue(constraints.packageCoupling().get().adp().isPresent());
        assertTrue(constraints.packageCoupling().get().racd().isPresent());
        assertTrue(constraints.packageCoupling().get().grouping().isPresent());
        assertTrue(constraints.submoduleDefinitions().isPresent());
    }
}

