package org.tindalos.principle.domain.analyzers.adp;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.tindalos.principle.domain.plan.AnalysisInput;
import org.tindalos.principle.domain.analyzers.Analyzer;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.core.CyclesInSubgraph;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageStructureBuilder;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;

/**
 * Analyzer for the Acyclic Dependency Principle (ADP).
 * Builds the package tree for the configured base package and detects cycles.
 */
public final class CycleDetector implements Analyzer {

    private final PackageStructureBuilder packageStructureBuilder;

    public CycleDetector(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = Objects.requireNonNull(packageStructureBuilder, "packageStructureBuilder");
    }

    @Override
    public ADPResult analyze(AnalysisInput input) {
        var basePackage = packageStructureBuilder.build(toPackages(input.packages()), input.analysisPlan().basePackage());
        var references = basePackage.toMap();
        
        var sortedByAfferents = references.values().stream()
            .sorted(Comparator.comparingInt(pkg -> pkg.getMetrics().afferentCoupling()))
            .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));

        if (basePackage.getMetrics().afferentCoupling() == 0) {
            sortedByAfferents.removeIf(basePackage::equals);
        }
        
        var cycles = analyzeCyclesRecursively(
            sortedByAfferents, 
            references, 
            new CyclesInSubgraph(Set.of(), Map.of()));

        var expectation = input.packageCouplingExpectations().flatMap(packageCoupling -> packageCoupling.adp()).get();
        return new ADPResult(cycles.cycles(), expectation);
    }

    /**
     * Recursively analyzes cycles by processing packages in sorted order,
     * removing already-investigated packages to avoid redundant analysis.
     */
    private CyclesInSubgraph analyzeCyclesRecursively(
        java.util.List<Package> remaining,
        Map<PackageReference, Package> references,
        CyclesInSubgraph accumulator) {
        
        if (remaining.isEmpty()) {
            return accumulator;
        }
        
        var current = remaining.get(0);
        var cyclesInSubgraph = current.detectCycles(references);
        var updatedAccumulator = accumulator.mergedWith(cyclesInSubgraph);
        
        var investigatedPackages = cyclesInSubgraph.investigatedPackages();
        var next = remaining.stream()
            .skip(1)
            .filter(pkg -> !investigatedPackages.contains(pkg))
            .toList();
        
        return analyzeCyclesRecursively(new java.util.ArrayList<>(next), references, updatedAccumulator);
    }

    @Override
    public boolean isEnabled(Constraints constraints) {
        return constraints.packageCoupling().isPresent()
                && constraints.packageCoupling().get().adp().isPresent();
    }

    private List<Package> toPackages(List<PackageWithMetrics> packages) {
        return packages.stream()
                .map(CycleDetector::toPackage)
                .toList();
    }

    private static Package toPackage(PackageWithMetrics packageWithMetrics) {
        Objects.requireNonNull(packageWithMetrics, "packageWithMetrics");
        if (packageWithMetrics instanceof Package aPackage) {
            return aPackage;
        }

        throw new IllegalArgumentException(
                "Expected %s but got %s".formatted(
                        Package.class.getName(),
                        packageWithMetrics.getClass().getName()));
    }
}