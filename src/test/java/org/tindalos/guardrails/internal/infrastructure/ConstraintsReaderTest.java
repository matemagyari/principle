package org.tindalos.guardrails.internal.infrastructure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.constraints.exception.InvalidConfigurationException;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceId;
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
    public void slices_isParsed() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  slices:
                    - name: layers
                      violation_threshold: 1
                      slices:
                        infrastructure: [infrastructure]
                        app: [app]
                        domain: [domain]
                      dependencies:
                        infrastructure: [app, domain]
                        app: [domain]
                        domain: []
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        var slices = constraints.slices().get();
        assertEquals(1, slices.sliceGroups().size());

        var group = slices.sliceGroups().get(0);
        assertEquals("layers", group.name());
        assertEquals(1, group.violationThreshold());
        assertEquals(3, group.slices().size());

        var infraItem = group.slices().get(new SliceId("infrastructure"));
        assertNotNull(infraItem);
        assertEquals(1, infraItem.packages().size());
        assertEquals(2, infraItem.legalDependencies().size());
        assertTrue(infraItem.legalDependencies().contains(new SliceId("app")));
        assertTrue(infraItem.legalDependencies().contains(new SliceId("domain")));
    }

    @Test
    public void noSlices_slicesIsAbsent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                """);

        var constraints = ConstraintsReader.readFromFile(Optional.of(path)).constraints();

        assertTrue(constraints.slices().isEmpty());
    }

    @Test
    public void noPackageCoupling_packageCouplingIsAbsent() throws Exception {
        var path = writeTempYaml("""
                root_package: com.example
                constraints:
                  slices:
                    - name: layers
                      slices:
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
                  slices:
                    - name: layers
                      slices:
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
        assertEquals("layers.infrastructure", tp.get().barriers().get(0).slice());
        assertEquals(List.of("org.apache.commons", "com.google"), tp.get().barriers().get(0).components());
        assertEquals("layers.app", tp.get().barriers().get(1).slice());
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
                  slices:
                    - name: layers
                      slices:
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
                    acd_threshold: 0.5
                    structure_analysis_enabled: true
                """);

        var plan = ConstraintsReader.readFromFile(Optional.of(path));
        var constraints = plan.constraints();

        assertEquals("org.example.myapp", plan.basePackage());
        assertTrue(constraints.slices().isPresent());
        assertTrue(constraints.thirdParty().isPresent());
        assertTrue(constraints.packageCoupling().isPresent());
        assertTrue(constraints.packageCoupling().get().adp().isPresent());
        assertTrue(constraints.packageCoupling().get().racd().isPresent());
        assertTrue(constraints.packageCoupling().get().grouping().isPresent());
    }
}