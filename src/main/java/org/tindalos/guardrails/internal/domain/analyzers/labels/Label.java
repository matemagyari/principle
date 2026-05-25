package org.tindalos.guardrails.internal.domain.analyzers.labels;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.tindalos.guardrails.internal.domain.constraints.labels.InvalidLabelDefinitionException;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelId;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Concrete representation of a label in a label group, combining the definition
 * with actual project packages and calculating illegal or missing dependencies.
 */
public class Label {

    public final LabelId id;
    public final Set<PackageWithMetrics> packagesUnderLabel;
    public final Set<LabelId> plannedDependencies;
    private final Set<PackageReference> outgoingReferences;

    public Label(LabelId id, Set<PackageWithMetrics> packagesUnderLabel, Set<LabelId> plannedDependencies) {
        if (plannedDependencies.contains(id)) {
            throw new InvalidLabelDefinitionException("Label should not depend on itself: " + id.value());
        }
        this.id = id;
        this.packagesUnderLabel = Set.copyOf(packagesUnderLabel);
        this.plannedDependencies = Set.copyOf(plannedDependencies);
        this.outgoingReferences = this.packagesUnderLabel.stream()
                .flatMap(p -> p.accumulatedDirectPackageReferences().stream())
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Label> findMissingPredefinedDependencies(Set<Label> otherLabels) {
        assert !otherLabels.contains(this);
        return otherLabels.stream()
                .filter(x -> plannedDependencies.contains(x.id))
                .filter(x -> !x.isReferredBy(outgoingReferences))
                .collect(Collectors.toUnmodifiableSet());
    }

    public Set<Label> findIllegalDependencies(Set<Label> otherLabels) {
        assert !otherLabels.contains(this);
        boolean hasIllegalDependencies = otherLabels.stream()
                .anyMatch(x -> !plannedDependencies.contains(x.id));
        if (!hasIllegalDependencies) return Set.of();
        Set<Label> legalDependencies = otherLabels.stream()
                .filter(x -> plannedDependencies.contains(x.id))
                .collect(Collectors.toSet());
        Set<PackageReference> extraReferences = calculateExtraReferences(legalDependencies);
        return otherLabels.stream()
                .filter(x -> x.isReferredBy(extraReferences))
                .collect(Collectors.toUnmodifiableSet());
    }

    private boolean isReferredBy(Set<PackageReference> references) {
        return references.stream().anyMatch(ref ->
                packagesUnderLabel.stream().anyMatch(pkg ->
                        ref.pointsToThatOrInside(pkg.reference())));
    }

    private Set<PackageReference> calculateExtraReferences(Set<Label> legalDependencies) {
        Set<PackageReference> potentiallyIllegal = new java.util.HashSet<>(outgoingReferences);
        for (Label legalDependency : legalDependencies) {
            potentiallyIllegal = legalDependency.removeOutsideReferences(potentiallyIllegal);
        }
        return potentiallyIllegal;
    }

    private Set<PackageReference> removeOutsideReferences(Set<PackageReference> references) {
        return references.stream()
                .filter(ref -> packagesUnderLabel.stream()
                        .noneMatch(pkg -> ref.pointsToThatOrInside(pkg.reference())))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Label)) return false;
        return ((Label) other).id.equals(id);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).hashCode();
    }

    @Override
    public String toString() {
        return id.value();
    }
}
