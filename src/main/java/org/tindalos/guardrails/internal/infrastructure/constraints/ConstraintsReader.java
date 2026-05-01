package org.tindalos.guardrails.internal.infrastructure.constraints;

import org.apache.commons.io.FileUtils;
import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleDefinitions;
import org.tindalos.guardrails.internal.domain.constraints.*;
import org.tindalos.guardrails.internal.domain.constraints.exception.InvalidConfigurationException;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.analyzers.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * Reads and parses the Principle configuration YAML file into an {@link AnalysisPlan}.
 * Supports layering, third-party restrictions, package coupling, and submodule definitions.
 */
public class ConstraintsReader {

    private static final String DEFAULT_FILE_LOCATION = "/guardrails.yml";

    private static final LayeringReader LAYERING_READER = new LayeringReader();
    private static final ThirdPartyReader THIRD_PARTY_READER = new ThirdPartyReader();
    private static final PackageCouplingConstraintsReader PACKAGE_COUPLING_READER = new PackageCouplingConstraintsReader();

    public static AnalysisPlan readFromFile(Optional<String> fileLocation) {
        var location = fileLocation.orElse(DEFAULT_FILE_LOCATION);
        return fromYaml(readYAML(location), location);
    }

    @SuppressWarnings("unchecked")
    private static AnalysisPlan fromYaml(String yamlText, String fileLocation) {
        Map<String, Object> yamlObject = (Map<String, Object>) new Yaml().load(yamlText);

        var rootPackage = (String) yamlObject.get("root_package");

        Map <String, Object> constraintsYaml = getYamlStructure(yamlObject, "constraints").orElseThrow();

        var modules = parseModules(yamlObject);

        var constraints = new Constraints(
            LAYERING_READER.read(yamlObject),
            THIRD_PARTY_READER.read(yamlObject),
            PACKAGE_COUPLING_READER.read(yamlObject),
            modules);

        return new AnalysisPlan(constraints, rootPackage);
    }

    private static Optional<SubmoduleDefinitions> parseModules(Map<String, Object> yamlObject) {
        return new YAMLBasedSubmodulesBlueprintProvider().read(yamlObject);
    }

    @SuppressWarnings("unchecked")
    private static Optional<Map<String, Object>> getYamlStructure(Map<String, Object> structure, String field) {
        return Optional.ofNullable((Map<String, Object>) structure.get(field));
    }

    private static String readYAML(String fileLocation) {
        try {
            return FileUtils.readFileToString(new File(fileLocation), java.nio.charset.StandardCharsets.UTF_8.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new InvalidConfigurationException("problem with reading file from " + fileLocation);
        }
    }
}

