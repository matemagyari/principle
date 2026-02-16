package org.tindalos.principle.domain.core;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a cyclic dependency between packages.
 * A cycle is a sequence of package references where the last package depends on the first,
 * creating a circular dependency chain.
 */
public class Cycle implements Comparable<Cycle> {

    private final List<PackageReference> references;
    private final PackageReference end;

    public Cycle(List<PackageReference> references) {
        if (references == null || references.size() < 2) {
            throw new DomainException("Invalid cycle " + references);
        }
        this.references = List.copyOf(references);
        this.end = references.getLast();
    }

    public Cycle(PackageReference... refs) {
        this(Arrays.asList(refs));
    }

    public List<PackageReference> references() {
        return references;
    }

    public PackageReference end() {
        return end;
    }

    @Override
    public String toString() {
        String arrow = "-->";
        StringBuilder sb = new StringBuilder("*" + arrow);
        for (PackageReference reference : references) {
            sb.append(reference).append(arrow);
        }
        sb.append("*");
        return sb.toString();
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Cycle castOther)) {
            return false;
        }

        // First check if they have the same set of references
        Set<PackageReference> thisSet = new HashSet<>(references);
        Set<PackageReference> otherSet = new HashSet<>(castOther.references);
        if (!thisSet.equals(otherSet)) {
            return false;
        }

        // Check if one is a rotation of the other
        int offset = castOther.references.indexOf(references.getFirst());
        if (offset == -1) {
            return false;
        }

        for (int i = 0; i < references.size(); i++) {
            int indexWithOffset = (i + offset) % references.size();
            if (!references.get(i).equals(castOther.references.get(indexWithOffset))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(references.size()).hashCode();
    }

    @Override
    public int compareTo(Cycle that) {
        return this.toString().compareTo(that.toString());
    }

}

