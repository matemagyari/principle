package org.tindalos.guardrails.internal.infrastructure.core;

import org.apache.commons.io.FileUtils;
import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleDefinitions;
import org.tindalos.guardrails.internal.domain.constraints.*;
import org.tindalos.guardrails.internal.domain.constraints.exception.InvalidConfigurationException;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.analyzers.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Reads and parses the Principle configuration YAML file into an {@link AnalysisPlan}.
 * Supports layering, third-party restrictions, package coupling, and submodule definitions.
 */
public class ConstraintsReader {

    private static final String DEFAULT_FILE_LOCATION = "/guardrails.yml";

    public static AnalysisPlan readFromFile(Optional<String> fileLocation) {
        var location = fileLocation.orElse(DEFAULT_FILE_LOCATION);
        return fromYaml(readYAML(location), location);
    }

    @SuppressWarnings("unchecked")
    private static AnalysisPlan fromYaml(String yamlText, String fileLocation) {
        Map<String, Object> yamlObject = (Map<String, Object>) new Yaml().load(yamlText);

        var rootPackage = (String) yamlObject.get("root_package");

        var constraintsYaml = getYamlStructure(yamlObject, "constraints").orElseThrow();

        var modules = parseModules(constraintsYaml, rootPackage, fileLocation);
        var packageCoupling = parsePackageCoupling(constraintsYaml, yamlObject);

        var constraints = new Constraints(
                getYamlStructure(constraintsYaml, "layering").map(ConstraintsReader::toLayering),
                getYamlStructure(constraintsYaml, "third_party_restrictions").map(ConstraintsReader::toThirdParty),
                Optional.of(packageCoupling),
                modules);

        return new AnalysisPlan(constraints, rootPackage);
    }

    @SuppressWarnings("unchecked")
    private static Optional<SubmoduleDefinitions> parseModules(Map<String, Object> constraintsYaml,
                                                                String rootPackage,
                                                                String fileLocation) {
        return getYamlStructure(constraintsYaml, "modules")
                .filter(m -> m.containsKey("module-definitions"))
                .map(modules -> {
                    var threshold = Optional.ofNullable((Integer) modules.get("violation_threshold")).orElse(0);
                    return new YAMLBasedSubmodulesBlueprintProvider()
                            .readSubmoduleDefinitions(rootPackage, fileLocation, threshold);
                });
    }

    @SuppressWarnings("unchecked")
    private static PackageCouplingConstraints parsePackageCoupling(Map<String, Object> constraintsYaml,
                                                                    Map<String, Object> yamlObject) {
        var builder = PackageCouplingConstraints.builder();

        getYamlStructure(constraintsYaml, "package_coupling").ifPresent(pc -> {
            Optional.ofNullable(pc.get("acd_threshold"))
                    .map(t -> new RACD((Double) t))
                    .ifPresent(builder::racd);
            Optional.ofNullable(pc.get("cyclic_dependencies_threshold"))
                    .map(t -> new ADP((Integer) t))
                    .ifPresent(builder::adp);
        });

        Optional.ofNullable(yamlObject.get("structure_analysis_enabled"))
                .filter(v -> (Boolean) v)
                .map(v -> Grouping.of())
                .ifPresent(builder::grouping);

        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static ThirdParty toThirdParty(Map<String, Object> structure) {
        var barriersYaml = (List<Map<String, Object>>) structure.get("allowed_libraries");
        var barriers = barriersYaml.stream()
                .map(m -> new Barrier(
                        (String) m.get("layer"),
                        (List<String>) m.get("libraries")))
                .toList();
        return new ThirdParty(barriers, (Integer) structure.get("violation_threshold"));
    }

    @SuppressWarnings("unchecked")
    private static Layering toLayering(Map<String, Object> structure) {
        var layers = Optional.ofNullable((List<String>) structure.get("layers"))
                .orElse(List.of());
        var threshold = Optional.ofNullable((Integer) structure.get("violation_threshold")).orElse(0);
        return new Layering(layers, threshold);
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

