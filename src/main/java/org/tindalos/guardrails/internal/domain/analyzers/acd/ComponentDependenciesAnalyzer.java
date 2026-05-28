package org.tindalos.guardrails.internal.domain.analyzers.acd;

import java.util.List;
import java.util.NoSuchElementException;

import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;
import org.tindalos.guardrails.internal.domain.analyzers.Analyzer;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuilder;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Calculates component dependency metrics (ACD/RACD/NCCD) for relevant packages.
 */
public class ComponentDependenciesAnalyzer implements Analyzer {

    private final PackageStructureBuilder packageStructureBuilder;

    public ComponentDependenciesAnalyzer(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    @Override
    public ComponentDependenciesResult analyze(AnalysisInput checkInput) {
        List<PackageWithMetrics> packages = checkInput.packages();

        var basePackage = packageStructureBuilder.build(
                packages.stream().map(pkg -> (Package) pkg).toList(),
                checkInput.analysisPlan().basePackage());

        var referenceMap = basePackage.toMap();

        List<PackageWithMetrics> relevantPackages = basePackage.metrics().isIsolated()
                ? packages.stream().filter(pkg -> !pkg.equals(basePackage)).toList()
                : packages;

        int cumulatedComponentDependency = relevantPackages.stream()
                .map(pkg -> (Package) pkg)
                .mapToInt(pkg -> pkg.cumulatedDependencies(referenceMap).size() + 1)
                .sum();

        return new ComponentDependenciesResult(
                cumulatedComponentDependency,
                relevantPackages.size(),
                checkInput.packageCouplingExpectations()
                        .orElseThrow(() -> new NoSuchElementException("Package coupling constraints are required")));
    }

    @Override
    public boolean isEnabled(Constraints constraints) {
        return constraints.packageCoupling()
                .map(pc -> pc.acd().isPresent() || pc.racd().isPresent() || pc.nccd().isPresent())
                .orElse(false);
    }
}
