package org.tindalos.guardrails.internal.domain.analyzers.acd;

import org.tindalos.guardrails.internal.domain.core.AnalysisResult;
import org.tindalos.guardrails.internal.domain.constraints.DoubleConstraint;
import org.tindalos.guardrails.internal.domain.constraints.PackageCouplingConstraints;

/**
 * Represents the result of an Average Component Dependency (ACD) analysis.
 * Contains computed coupling metrics and evaluates them against configured thresholds.
 *
 * @param cumulatedComponentDependency sum of all component dependencies across packages
 * @param numOfComponents              total number of components analyzed
 * @param packageCoupling              the coupling constraints to validate against
 */
public record ComponentDependenciesResult(
        int cumulatedComponentDependency,
        int numOfComponents,
        PackageCouplingConstraints packageCoupling) implements AnalysisResult {

    public double acd() {
        return (double) cumulatedComponentDependency / numOfComponents;
    }

    public double rAcd() {
        return acd() / numOfComponents;
    }

    public double nCcd() {
        return acd() / numOfComponents;
    }

    @Override
    public boolean constraintViolated() {
        return greaterIfExists(rAcd(), packageCoupling.racd().orElse(null)) ||
               greaterIfExists(nCcd(), packageCoupling.nccd().orElse(null));
    }

    public double getRACDThreshold() {
        return packageCoupling.racd()
                .map(DoubleConstraint::threshold)
                .orElse(999.0);
    }

    private boolean greaterIfExists(double actual, DoubleConstraint expectation) {
        return expectation != null &&
               !Double.isNaN(expectation.threshold()) &&
               actual > expectation.threshold();
    }
}

