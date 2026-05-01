package org.tindalos.guardrails.internal.domain.analyzers.submodulesblueprint;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.domain.constraints.submodules.InvalidBlueprintDefinitionException;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuilder;
import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleDefinition;
import org.tindalos.guardrails.internal.domain.constraints.submodules.SubmoduleDefinitions;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Builds concrete submodules from blueprint definitions and discovered packages.
 */
public class SubmodulesBuilder {

    private final PackageStructureBuilder packageStructureBuilder;

    public SubmodulesBuilder(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    public Set<Submodule> build(
        SubmoduleDefinitions submoduleDefinitions,
        List<PackageWithMetrics> packages,
        String basePackageName) {

        submoduleDefinitions.checkNoOverlaps();

        Package basePackage = packageStructureBuilder.build(toPackageList(packages), basePackageName);
        Map<PackageReference, Package> packageMap = basePackage.toMap();

        return submoduleDefinitions.definitions().values().stream()
            .map(definition -> convert(definition, packageMap))
            .collect(Collectors.toUnmodifiableSet());
    }

    private Submodule convert(SubmoduleDefinition definition, Map<PackageReference, Package> packageMap) {
        Set<PackageWithMetrics> packagesUnderModule = definition.packages().stream()
            .map(reference -> resolvePackage(reference, packageMap))
            .collect(Collectors.toCollection(HashSet::new));

        return new Submodule(
            definition.id(),
            Set.copyOf(packagesUnderModule),
            definition.getLegalDependencies());
    }

    private PackageWithMetrics resolvePackage(PackageReference reference, Map<PackageReference, Package> packageMap) {
        Package foundPackage = packageMap.get(reference);
        if (foundPackage == null) {
            throw new InvalidBlueprintDefinitionException("Package does not exist: " + reference);
        }
        return foundPackage;
    }

    private List<Package> toPackageList(List<PackageWithMetrics> packages) {
        return packages.stream()
            .map(pkg -> (Package) pkg)
            .collect(Collectors.toUnmodifiableList());
    }
}
