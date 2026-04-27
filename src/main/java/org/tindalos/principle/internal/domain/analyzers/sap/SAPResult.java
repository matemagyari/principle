package org.tindalos.principle.internal.domain.analyzers.sap;

import org.tindalos.principle.internal.domain.core.AnalysisResult;
import org.tindalos.principle.internal.domain.constraints.SAP;
import org.tindalos.principle.internal.domain.core.packages.PackageWithMetrics;

import java.util.List;

/**
 * Represents the result of a Stable Abstractions Principle (SAP) analysis.
 * Contains packages whose distance from the main sequence exceeds the configured threshold.
 *
 * @param outlierPackages list of packages that violate the SAP constraint
 * @param sapExpectation  the SAP constraint configuration
 */
public record SAPResult(
        List<PackageWithMetrics> outlierPackages,
        SAP sapExpectation) implements AnalysisResult {

    public SAPResult {
        outlierPackages = List.copyOf(outlierPackages);
    }

    public int threshold() {
        return sapExpectation.violationThreshold();
    }

    @Override
    public boolean constraintViolated() {
        return outlierPackages.size() > threshold();
    }
}

