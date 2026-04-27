package org.tindalos.principle.internal.domain.analyzers.sdp;

import org.tindalos.principle.internal.domain.core.AnalysisResult;
import org.tindalos.principle.internal.domain.constraints.SDP;

import java.util.List;

/**
 * Represents the result of a Stable Dependencies Principle (SDP) analysis.
 * Contains violations where a package depends on another with higher instability.
 *
 * @param violations  immutable list of detected SDP violations
 * @param expectation the SDP constraint configuration
 */
public record SDPResult(
        List<SDPViolation> violations,
        SDP expectation) implements AnalysisResult {

    public SDPResult {
        violations = List.copyOf(violations);
    }

    public int threshold() {
        return expectation.violationThreshold();
    }

    @Override
    public boolean constraintViolated() {
        return violations.size() > threshold();
    }
}

