package org.tindalos.guardrails.internal.infrastructure.constraints;

import org.apache.commons.io.FileUtils;
import org.tindalos.guardrails.internal.domain.constraints.*;
import org.tindalos.guardrails.internal.domain.constraints.exception.InvalidConfigurationException;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * Reads and parses the Guardrails configuration YAML file into an {@link AnalysisPlan}.
 * Supports layering, third-party restrictions, package coupling, and submodule definitions.
 */
public class ConstraintsReader {

    private static final String DEFAULT_FILE_LOCATION = "/guardrails.yml";

    private static final LayeringReader LAYERING_READER = new LayeringReader();
    private static final ThirdPartyReader THIRD_PARTY_READER = new ThirdPartyReader();
    private static final PackageCouplingConstraintsReader PACKAGE_COUPLING_READER = new PackageCouplingConstraintsReader();
    private static final SubmodulesBlueprintReader SUBMODULES_BLUEPRINT_READER = new SubmodulesBlueprintReader();

    public static AnalysisPlan readFromFile(Optional<String> fileLocation) {
        var location = fileLocation.orElse(DEFAULT_FILE_LOCATION);
        return fromYaml(readYAML(location));
    }

    @SuppressWarnings("unchecked")
    private static AnalysisPlan fromYaml(String yamlText) {
        Map<String, Object> yamlObject = (Map<String, Object>) new Yaml().load(yamlText);

        var constraints = new Constraints(
                LAYERING_READER.read(yamlObject),
                THIRD_PARTY_READER.read(yamlObject),
                PACKAGE_COUPLING_READER.read(yamlObject),
                SUBMODULES_BLUEPRINT_READER.read(yamlObject));

        return new AnalysisPlan(constraints, (String) yamlObject.get("root_package"));
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

