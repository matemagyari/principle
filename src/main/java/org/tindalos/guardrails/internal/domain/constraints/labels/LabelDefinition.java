package org.tindalos.guardrails.internal.domain.constraints.labels;

import java.util.Set;

import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;

/**
 * Represents a label definition in a guardrails configuration, containing the label's ID,
 * the packages it includes, and its allowed dependencies on other labels.
 */
public record LabelDefinition(
        LabelId id,
        Set<PackageReference> packages,
        Set<LabelId> legalDependencies) {

    public LabelDefinition {
        packages = Set.copyOf(packages);
        legalDependencies = Set.copyOf(legalDependencies);
    }

    public boolean overlapsWith(LabelDefinition that) {
        for (PackageReference aPackage : this.packages) {
            for (PackageReference otherPackage : that.packages) {
                if (aPackage.oneContainsAnother(otherPackage)) {
                    return true;
                }
            }
        }
        return false;
    }
}
