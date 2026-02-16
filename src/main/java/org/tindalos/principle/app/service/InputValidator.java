package org.tindalos.principle.app.service;

import org.tindalos.principle.domain.core.AnalysisPlan;

/**
 * Validates analysis plan inputs to ensure consistency and correctness
 * of configuration before running architectural analysis.
 */
public interface InputValidator {

    /**
     * Validates the analysis plan configuration.
     *
     * @param plan the analysis plan to validate
     * @return validation result indicating success or failure with error message
     */
    ValidationResult validate(AnalysisPlan plan);

}

