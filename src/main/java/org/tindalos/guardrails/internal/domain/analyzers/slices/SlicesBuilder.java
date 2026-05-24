package org.tindalos.guardrails.internal.domain.analyzers.slices;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.domain.constraints.slices.InvalidSliceDefinitionException;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceDefinition;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceGroup;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuilder;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Builds concrete slice instances from design slice definitions and scanned package hierarchies.
 */
public class SlicesBuilder {

    private final PackageStructureBuilder packageStructureBuilder;

    public SlicesBuilder(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    public Set<Slice> build(
            SliceGroup sliceGroup,
            List<PackageWithMetrics> packages,
            String basePackageName) {

        sliceGroup.checkNoOverlaps();

        Package basePackage = packageStructureBuilder.build(toPackageList(packages), basePackageName);
        Map<PackageReference, Package> packageMap = basePackage.toMap();

        return sliceGroup.slices().values().stream()
                .map(definition -> convert(definition, packageMap))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Slice convert(SliceDefinition definition, Map<PackageReference, Package> packageMap) {
        Set<PackageWithMetrics> packagesUnderSlice = definition.packages().stream()
                .map(reference -> resolvePackage(reference, packageMap))
                .collect(Collectors.toCollection(HashSet::new));

        return new Slice(
                definition.id(),
                Set.copyOf(packagesUnderSlice),
                definition.legalDependencies());
    }

    private PackageWithMetrics resolvePackage(PackageReference reference, Map<PackageReference, Package> packageMap) {
        Package foundPackage = packageMap.get(reference);
        if (foundPackage == null) {
            throw new InvalidSliceDefinitionException("Package does not exist: " + reference);
        }
        return foundPackage;
    }

    private List<Package> toPackageList(List<PackageWithMetrics> packages) {
        return packages.stream()
                .map(pkg -> (Package) pkg)
                .collect(Collectors.toUnmodifiableList());
    }
}
