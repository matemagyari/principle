package org.tindalos.principle.domain.analyzers.layering;

import org.tindalos.principle.domain.AnalysisResult;

import java.util.List;

/**
 * Represents the result of a layering constraint analysis.
 * Contains all detected layer violations and the allowed threshold.
 *
 * @param violations list of illegal layer references detected
 * @param threshold  the maximum number of allowed violations
 */
public record LayerViolationsResult(
        List<LayerReference> violations,
        int threshold) implements AnalysisResult {

    @Override
    public boolean constraintViolated() {
        return violations.size() > threshold;
    }
}

