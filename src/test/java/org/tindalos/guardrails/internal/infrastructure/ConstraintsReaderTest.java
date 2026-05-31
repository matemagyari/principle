package org.tindalos.guardrails.internal.infrastructure;

import java.io.File;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.constraints.exception.InvalidConfigurationException;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelId;
import org.tindalos.guardrails.internal.infrastructure.constraints.ConstraintsReader;

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
    public void labels_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  labels:
                    - name: layers
                      violation_threshold: 1
                      labels:
                        infrastructure: [infrastructure]
                        app: [app]
                        domain: [domain]
                      dependencies:
                        infrastructure: [app, domain]
                        app: [domain]
                        domain: []
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        var labels = constraints.labels().get();
        assertEquals(1, labels.labelGroups().size());

        var group = labels.labelGroups().get(0);
        assertEquals("layers", group.name());
        assertEquals(1, group.violationThreshold());
        assertEquals(3, group.labels().size());

        var infraItem = group.labels().get(new LabelId("infrastructure"));
        assertNotNull(infraItem);
        assertEquals(1, infraItem.packages().size());
        assertEquals(2, infraItem.legalDependencies().size());
        assertTrue(infraItem.legalDependencies().contains(new LabelId("app")));
        assertTrue(infraItem.legalDependencies().contains(new LabelId("domain")));
    }

    @Test
    public void noLabels_labelsIsAbsent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertTrue(constraints.labels().isEmpty());
    }

    @Test
    public void noPackageCoupling_packageCouplingIsAbsent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  labels:
                    - name: layers
                      labels:
                        domain: [domain]
                      dependencies:
                        domain: []
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertTrue(constraints.packageCoupling().isEmpty());
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
    public void packageCoupling_sdp_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    sdp:
                      violation_threshold: 4
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        var sdp = constraints.packageCoupling().get().sdp();
        assertTrue(sdp.isPresent());
        assertEquals(4, sdp.get().violationThreshold());
    }

    @Test
    public void packageCoupling_sap_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    sap:
                      violation_threshold: 2
                      max_distance: 0.25
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        var sap = constraints.packageCoupling().get().sap();
        assertTrue(sap.isPresent());
        assertEquals(2, sap.get().violationThreshold());
        assertEquals(0.25, sap.get().maxDistance(), 0.001);
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
    public void structureAnalysisEnabledWithoutPackageCoupling_groupingOnlyPackageCouplingIsPresent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  labels:
                    - name: layers
                      labels:
                        domain: [domain]
                      dependencies:
                        domain: []
                  package_coupling:
                    structure_analysis_enabled: true
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertTrue(constraints.packageCoupling().isPresent());
        assertTrue(constraints.packageCoupling().get().grouping().isPresent());
        assertTrue(constraints.packageCoupling().get().adp().isEmpty());
        assertTrue(constraints.packageCoupling().get().racd().isEmpty());
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
                      - layers.infrastructure: [org.apache.commons, com.google]
                      - layers.app: [org.apache.commons]
                    violation_threshold: 3
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        var tp = constraints.thirdParty();
        assertTrue(tp.isPresent());
        assertEquals(3, tp.get().violationThreshold());
        assertEquals(2, tp.get().barriers().size());
        assertEquals("layers.infrastructure", tp.get().barriers().get(0).label());
        assertEquals(List.of("org.apache.commons", "com.google"), tp.get().barriers().get(0).components());
        assertEquals("layers.app", tp.get().barriers().get(1).label());
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
    public void missingFile_throwsInvalidConfigurationException() {
      assertThrows(InvalidConfigurationException.class,
          () -> ConstraintsReader.readFromFile(Optional.of("/non/existent/path/guardrails.yml")));
    }

    @Test
    public void fullConfig_isParsedCorrectly() throws Exception {
        var path = writeTempYaml("""
                root_package: org.example.myapp
                constraints:
                  labels:
                    - name: layers
                      labels:
                        infrastructure: [infrastructure]
                        app: [app]
                        domain: [domain]
                      dependencies:
                        infrastructure: [app]
                        app: [domain]
                        domain: []
                      violation_threshold: 0
                  third_party_restrictions:
                    allowed_libraries:
                      - layers.infrastructure: [org.apache.commons]
                    violation_threshold: 0
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                    sdp:
                      violation_threshold: 1
                    sap:
                      violation_threshold: 2
                      max_distance: 0.2
                    acd_threshold: 0.5
                    structure_analysis_enabled: true
                """);

        var plan = ConstraintsReader.readFromFile(Optional.of(path));
        var constraints = plan.constraints();

        assertEquals("org.example.myapp", plan.basePackage());
        assertTrue(constraints.labels().isPresent());
        assertTrue(constraints.thirdParty().isPresent());
        assertTrue(constraints.packageCoupling().isPresent());
        assertTrue(constraints.packageCoupling().get().adp().isPresent());
        assertTrue(constraints.packageCoupling().get().sdp().isPresent());
        assertTrue(constraints.packageCoupling().get().sap().isPresent());
        assertTrue(constraints.packageCoupling().get().racd().isPresent());
        assertTrue(constraints.packageCoupling().get().grouping().isPresent());
    }
}