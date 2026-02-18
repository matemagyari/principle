package org.tindalos.principle.domain.analyzers.submodulesblueprint;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.tindalos.principle.domain.core.PackageReference;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a module definition in a blueprint, containing the module's ID,
 * the packages it includes, and its planned dependencies on other modules.
 */
public class SubmoduleDefinition {

    private final SubmoduleId id;
    private final Set<PackageReference> packages;
    private final Set<SubmoduleId> legalDependencies;

    public SubmoduleDefinition(SubmoduleId id, Set<PackageReference> packages) {
        this.id = id;
        this.packages = Set.copyOf(packages); // Defensive copy, immutable
        this.legalDependencies = new HashSet<>(); // Mutable
    }

    public SubmoduleId id() {
        return id;
    }

    public Set<PackageReference> packages() {
        return packages;
    }

    public void addPlannedDependencies(List<SubmoduleId> plannedDependencies) {
        legalDependencies.addAll(plannedDependencies);
    }

    public boolean overlapsWith(SubmoduleDefinition that) {
        for (PackageReference aPackage : this.packages) {
            for (PackageReference otherPackage : that.packages) {
                if (aPackage.oneContainsAnother(otherPackage)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<SubmoduleId> getLegalDependencies() {
        return Set.copyOf(legalDependencies);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SubmoduleDefinition)) {
            return false;
        }
        SubmoduleDefinition that = (SubmoduleDefinition) other;
        return this.id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).hashCode();
    }

    @Override
    public String toString() {
        return "SubmoduleDefinition [" + id + "]";
    }
}

