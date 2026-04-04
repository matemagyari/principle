package org.tindalos.principle.app;

import org.tindalos.principle.domain.plan.AnalysisPlan;

/**
 * Validates analysis plan to ensure consistency and correctness
 * of configuration before running architectural analysis.
 */
public interface AnalysisPlanValidator {

    /**
     * Validates the analysis plan configuration.
     *
     * @param plan the analysis plan to validate
     * @return validation result indicating success or failure with error message
     */
    ValidationResult validate(AnalysisPlan plan);

}

