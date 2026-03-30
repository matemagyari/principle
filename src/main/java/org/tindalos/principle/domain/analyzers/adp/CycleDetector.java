package org.tindalos.principle.domain.analyzers.adp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.principle.domain.AnalysisInput;
import org.tindalos.principle.domain.analyzers.Analyzer;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageStructureBuilder;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;

/**
 * Analyzer for the Acyclic Dependency Principle (ADP).
 * Builds the package tree for the configured base package and detects cycles
 * without relying on Scala collection conversions.
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
        Map<PackageReference, Set<Cycle>> cycles = new HashMap<>();

        var sortedByAfferents = references.values().stream()
                .sorted(Comparator.comparingInt(pkg -> pkg.getMetrics().afferentCoupling()))
            .collect(Collectors.toCollection(ArrayList::new));

        if (basePackage.getMetrics().afferentCoupling() == 0) {
            sortedByAfferents.removeIf(basePackage::equals);
        }

        while (!sortedByAfferents.isEmpty()) {
            var cyclesInSubgraph = sortedByAfferents.get(0).detectCycles(references);
            cycles = new HashMap<>(cyclesInSubgraph.mergeBreakingPoints2(cycles));

            var investigatedPackages = cyclesInSubgraph.investigatedPackages();
            sortedByAfferents.removeIf(investigatedPackages::contains);
        }

        var expectation = input.packageCouplingExpectations().flatMap(packageCoupling -> packageCoupling.adp()).get();
        return new ADPResult(cycles, expectation);
    }

    @Override
    public boolean isEnabled(Constraints expectations) {
        return expectations.packageCoupling().isPresent()
                && expectations.packageCoupling().get().adp().isPresent();
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