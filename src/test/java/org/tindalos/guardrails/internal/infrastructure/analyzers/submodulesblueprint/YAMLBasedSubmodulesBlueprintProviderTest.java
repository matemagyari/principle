package org.tindalos.guardrails.internal.infrastructure.analyzers.submodulesblueprint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.constraints.submodules.*;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for YAMLBasedSubmodulesBlueprintProvider to verify YAML parsing,
 * module definition creation, and error handling.
 */
public class YAMLBasedSubmodulesBlueprintProviderTest {

  private Map<String, Object> yamlObject;
    private YAMLBasedSubmodulesBlueprintProvider provider;

    @BeforeEach
    public void setUp() {
        String yaml = """
            root_package: com
            constraints:
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

            yamlObject = parseYaml(yaml);
        provider = new YAMLBasedSubmodulesBlueprintProvider();
    }

          @SuppressWarnings("unchecked")
          private Map<String, Object> parseYaml(String content) {
            return (Map<String, Object>) new Yaml().load(content);
    }

    @Test
    public void readSubmoduleDefinitions_validYaml_parsesSuccessfully() {
      SubmoduleDefinitions result = provider.readSubmoduleDefinitions(yamlObject);

        assertNotNull(result, "SubmoduleDefinitions should not be null");

        Map<SubmoduleId, SubmoduleDefinition> definitions = result.getDefinitions();
        assertEquals(3, definitions.size(), "Should have 3 module definitions");
        assertEquals(
                Set.of(new SubmoduleId("MOD1"), new SubmoduleId("MOD2"), new SubmoduleId("MOD3")),
                definitions.keySet(), "Should contain MOD1, MOD2, and MOD3");

        // Verify MOD1
        SubmoduleDefinition definition1 = definitions.get(new SubmoduleId("MOD1"));
        assertNotNull(definition1, "MOD1 should exist");
        assertEquals(
                Set.of(new SubmoduleId("MOD2"), new SubmoduleId("MOD3")),
                definition1.getLegalDependencies(), "MOD1 should have correct dependencies");
        assertEquals(
                Set.of(new PackageReference("com.domain.mod1"), new PackageReference("com.app.mod1")),
                definition1.packages(), "MOD1 should have correct packages");

        // Verify MOD2
        SubmoduleDefinition definition2 = definitions.get(new SubmoduleId("MOD2"));
        assertNotNull(definition2, "MOD2 should exist");
        assertEquals(
                Set.of(new SubmoduleId("MOD3")),
                definition2.getLegalDependencies(), "MOD2 should have correct dependencies");
        assertEquals(
                Set.of(new PackageReference("com.domain.mod2"), new PackageReference("com.app.mod2")),
                definition2.packages(), "MOD2 should have correct packages");

        // Verify MOD3
        SubmoduleDefinition definition3 = definitions.get(new SubmoduleId("MOD3"));
        assertNotNull(definition3, "MOD3 should exist");
        assertTrue(definition3.getLegalDependencies().isEmpty(), "MOD3 should have no dependencies");
        assertEquals(
                Set.of(new PackageReference("com.domain.mod3"), new PackageReference("com.app.mod3")),
                definition3.packages(), "MOD3 should have correct packages");
    }

    @Test
    public void readSubmoduleDefinitions_missingRootPackage_throwsException() {
      var yamlObject = parseYaml("""
          constraints:
            modules:
              module-definitions:
                MOD1: [domain.mod1]
              module-dependencies:
                MOD1: []
          """);

      assertThrows(InvalidBlueprintDefinitionException.class,
          () -> provider.readSubmoduleDefinitions(yamlObject));
    }

    @Test
    public void readSubmoduleDefinitions_missingModuleDefinitions_throwsException() {
        String yaml = """
            root_package: com
            constraints:
              modules:
                module-dependencies:
                  MOD1: [MOD2]
                violation_threshold: 0
            """;

        var yamlObject = parseYaml(yaml);
        assertThrows(InvalidBlueprintDefinitionException.class,
          () -> provider.readSubmoduleDefinitions(yamlObject));
    }

      @Test
    public void readSubmoduleDefinitions_missingModuleDependencies_throwsException() {
        String yaml = """
            root_package: com
            constraints:
              modules:
                module-definitions:
                  MOD1: [domain.mod1, app.mod1]
                  MOD2: [domain.mod2, app.mod2]
                violation_threshold: 0
            """;

        var yamlObject = parseYaml(yaml);
        assertThrows(InvalidBlueprintDefinitionException.class,
          () -> provider.readSubmoduleDefinitions(yamlObject));
    }

      @Test
    public void readSubmoduleDefinitions_overlappingModules_throwsOnOverlapCheck() {
        String yaml = """
            root_package: com
            constraints:
              modules:
                module-definitions:
                  MOD1: [domain]
                  MOD2: [domain.mod2]
                module-dependencies:
                  MOD1: []
                  MOD2: []
                violation_threshold: 0
            """;

        assertThrows(OverlappingSubmoduleDefinitionsException.class,
            () -> {
              var yamlObject = parseYaml(yaml);
              SubmoduleDefinitions definitions = provider.readSubmoduleDefinitions(yamlObject);
              definitions.checkNoOverlaps();
            });
    }
}
