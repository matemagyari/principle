package org.tindalos.guardrails.api;

import java.util.Map;
import java.util.Optional;

/**
 * Public reader contract for custom constraint/result definitions loaded from YAML.
 */
public interface ConstraintDefinitionReader<T extends AnalysisResult> {

    /**
     * Unique key used to expose this reader's parsed value through AnalysisPlan.customDefinition.
     */
    String key();

    Optional<T> read(Map<String, Object> yamlObject);
}
