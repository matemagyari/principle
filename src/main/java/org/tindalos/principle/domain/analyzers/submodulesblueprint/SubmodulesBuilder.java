package org.tindalos.principle.domain.analyzers.submodulesblueprint;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageStructureBuilder;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;

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

        Package basePackage = packageStructureBuilder.build(asScalaPackageList(packages), basePackageName);
        Map<PackageReference, Package> packageMap = basePackage.toMap();

        return submoduleDefinitions.getDefinitions().values().stream()
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

    @SuppressWarnings("unchecked")
    private scala.collection.immutable.List<Package> asScalaPackageList(List<PackageWithMetrics> packages) {
        scala.collection.immutable.List<Package> scalaPackages = scala.collection.immutable.List$.MODULE$.empty();

        for (int i = packages.size() - 1; i >= 0; i--) {
            scalaPackages = new scala.collection.immutable.$colon$colon<>(
                (Package) packages.get(i),
                scalaPackages);
        }

        return scalaPackages;
    }
}
