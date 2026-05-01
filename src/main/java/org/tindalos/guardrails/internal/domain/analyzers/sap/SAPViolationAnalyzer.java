package org.tindalos.guardrails.internal.domain.analyzers.sap;

import java.util.List;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.domain.analyzers.Analyzer;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;
import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;

/**
 * Analyzer for the Stable Abstractions Principle (SAP).
 */
public final class SAPViolationAnalyzer implements Analyzer {

    @Override
    public SAPResult analyze(AnalysisInput checkInput) {
        var sapExpectation = checkInput.packageCouplingExpectations().flatMap(pc -> pc.sap()).orElseThrow();
        var maxDistance = sapExpectation.maxDistance();

        var outlierPackages = removeRootPackageIfEmpty(checkInput.packages()).stream()
                .filter(pkg -> pkg.getMetrics().distance() > maxDistance)
                .collect(Collectors.toUnmodifiableList());

        return new SAPResult(outlierPackages, sapExpectation);
    }

    private List<PackageWithMetrics> removeRootPackageIfEmpty(List<PackageWithMetrics> packages) {
        var metrics = packages.get(0).getMetrics();
        if (metrics.abstractness() == 0 && metrics.instability() == 0) {
            return packages.subList(1, packages.size());
        }
        return packages;
    }

    @Override
    public boolean isEnabled(Constraints constraints) {
        return constraints.packageCoupling().flatMap(pc -> pc.sap()).isPresent();
    }
}