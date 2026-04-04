package org.tindalos.principle.infrastructure.analyzers.submodulesblueprint;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.domain.constraints.submodules.*;
import org.tindalos.principle.domain.core.packages.PackageReference;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests for YAMLBasedSubmodulesBlueprintProvider to verify YAML parsing,
 * module definition creation, and error handling.
 */
public class YAMLBasedSubmodulesBlueprintProviderTest {

    private String yamlFile;
    private YAMLBasedSubmodulesBlueprintProvider provider;

    @Before
    public void setUp() {
        String yaml = """
            checks:
              modules:
                module-definitions:
                  MOD1: [domain.mod1, app.mod1]
                  MOD2: [domain.mod2, app.mod2]
                  MOD3: [domain.mod3, app.mod3]
                module-dependencies:
                  MOD1: [MOD2, MOD3]
                  MOD2: [MOD3]
                  MOD3: []
                violation_threshold: 0
            """;

        yamlFile = createTempYamlFile(yaml);
        provider = new YAMLBasedSubmodulesBlueprintProvider();
    }

    private String createTempYamlFile(String content) {
        try {
            File tempFile = File.createTempFile("test_blueprint_", ".yaml");
            tempFile.deleteOnExit();
            FileUtils.writeStringToFile(tempFile, content);
            return tempFile.getAbsolutePath();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create temp YAML file", e);
        }
    }

    @Test
    public void readSubmoduleDefinitions_validYaml_parsesSuccessfully() {
        SubmoduleDefinitions result = provider.readSubmoduleDefinitions("com", yamlFile, 0);

        assertNotNull("SubmoduleDefinitions should not be null", result);

        Map<SubmoduleId, SubmoduleDefinition> definitions = result.getDefinitions();
        assertEquals("Should have 3 module definitions", 3, definitions.size());
        assertEquals("Should contain MOD1, MOD2, and MOD3",
                Set.of(new SubmoduleId("MOD1"), new SubmoduleId("MOD2"), new SubmoduleId("MOD3")),
                definitions.keySet());

        // Verify MOD1
        SubmoduleDefinition definition1 = definitions.get(new SubmoduleId("MOD1"));
        assertNotNull("MOD1 should exist", definition1);
        assertEquals("MOD1 should have correct dependencies",
                Set.of(new SubmoduleId("MOD2"), new SubmoduleId("MOD3")),
                definition1.getLegalDependencies());
        assertEquals("MOD1 should have correct packages",
                Set.of(new PackageReference("com.domain.mod1"), new PackageReference("com.app.mod1")),
                definition1.packages());

        // Verify MOD2
        SubmoduleDefinition definition2 = definitions.get(new SubmoduleId("MOD2"));
        assertNotNull("MOD2 should exist", definition2);
        assertEquals("MOD2 should have correct dependencies",
                Set.of(new SubmoduleId("MOD3")),
                definition2.getLegalDependencies());
        assertEquals("MOD2 should have correct packages",
                Set.of(new PackageReference("com.domain.mod2"), new PackageReference("com.app.mod2")),
                definition2.packages());

        // Verify MOD3
        SubmoduleDefinition definition3 = definitions.get(new SubmoduleId("MOD3"));
        assertNotNull("MOD3 should exist", definition3);
        assertTrue("MOD3 should have no dependencies", definition3.getLegalDependencies().isEmpty());
        assertEquals("MOD3 should have correct packages",
                Set.of(new PackageReference("com.domain.mod3"), new PackageReference("com.app.mod3")),
                definition3.packages());
    }

    @Test(expected = InvalidBlueprintDefinitionException.class)
    public void readSubmoduleDefinitions_missingFile_throwsException() {
        provider.readSubmoduleDefinitions("com", "src/test/resources/non_existent_file.yaml", 0);
    }

    @Test(expected = InvalidBlueprintDefinitionException.class)
    public void readSubmoduleDefinitions_missingModuleDefinitions_throwsException() {
        String yaml = """
            checks:
              modules:
                module-dependencies:
                  MOD1: [MOD2]
                violation_threshold: 0
            """;

        String yamlFile = createTempYamlFile(yaml);
        provider.readSubmoduleDefinitions("com", yamlFile, 0);
    }

    @Test(expected = InvalidBlueprintDefinitionException.class)
    public void readSubmoduleDefinitions_missingModuleDependencies_throwsException() {
        String yaml = """
            checks:
              modules:
                module-definitions:
                  MOD1: [domain.mod1, app.mod1]
                  MOD2: [domain.mod2, app.mod2]
                violation_threshold: 0
            """;

        String yamlFile = createTempYamlFile(yaml);
        provider.readSubmoduleDefinitions("com", yamlFile, 0);
    }

    @Test(expected = OverlappingSubmoduleDefinitionsException.class)
    public void readSubmoduleDefinitions_overlappingModules_throwsOnOverlapCheck() {
        String yaml = """
            checks:
              modules:
                module-definitions:
                  MOD1: [domain]
                  MOD2: [domain.mod2]
                module-dependencies:
                  MOD1: []
                  MOD2: []
                violation_threshold: 0
            """;

        String yamlFile = createTempYamlFile(yaml);
        SubmoduleDefinitions definitions = provider.readSubmoduleDefinitions("com", yamlFile, 0);
        definitions.checkNoOverlaps();
    }
}
