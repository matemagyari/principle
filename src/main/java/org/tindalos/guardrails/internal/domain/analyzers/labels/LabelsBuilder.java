package org.tindalos.guardrails.internal.domain.analyzers.labels;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tindalos.guardrails.internal.domain.constraints.labels.InvalidLabelDefinitionException;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelDefinition;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelGroup;
import org.tindalos.guardrails.internal.domain.core.Package;
import org.tindalos.guardrails.internal.domain.core.PackageStructureBuilder;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Builds concrete label instances from design label definitions and scanned package hierarchies.
 */
public class LabelsBuilder {

    private final PackageStructureBuilder packageStructureBuilder;

    public LabelsBuilder(PackageStructureBuilder packageStructureBuilder) {
        this.packageStructureBuilder = packageStructureBuilder;
    }

    public Set<Label> build(
            LabelGroup labelGroup,
            List<PackageWithMetrics> packages,
            String basePackageName) {

        labelGroup.checkNoOverlaps();

        Package basePackage = packageStructureBuilder.build(toPackageList(packages), basePackageName);
        Map<PackageReference, Package> packageMap = basePackage.toMap();

        return labelGroup.labels().values().stream()
                .map(definition -> convert(definition, packageMap))
                .collect(Collectors.toUnmodifiableSet());
    }

    private Label convert(LabelDefinition definition, Map<PackageReference, Package> packageMap) {
        Set<PackageWithMetrics> packagesUnderLabel = definition.packages().stream()
                .map(reference -> resolvePackage(reference, packageMap))
                .collect(Collectors.toCollection(HashSet::new));

        return new Label(
                definition.id(),
                Set.copyOf(packagesUnderLabel),
                definition.legalDependencies());
    }

    private PackageWithMetrics resolvePackage(PackageReference reference, Map<PackageReference, Package> packageMap) {
        Package foundPackage = packageMap.get(reference);
        if (foundPackage == null) {
            throw new InvalidLabelDefinitionException("Package does not exist: " + reference);
        }
        return foundPackage;
    }

    private List<Package> toPackageList(List<PackageWithMetrics> packages) {
        return packages.stream()
                .map(pkg -> (Package) pkg)
                .collect(Collectors.toUnmodifiableList());
    }
}
