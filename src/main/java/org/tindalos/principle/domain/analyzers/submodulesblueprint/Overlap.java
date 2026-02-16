package org.tindalos.principle.domain.analyzers.submodulesblueprint;

import java.util.Set;

/**
 * Represents an overlap between two submodules.
 * An overlap occurs when the same package is defined in multiple submodules.
 *
 * @param submodule1 the first submodule in the overlap
 * @param submodule2 the second submodule in the overlap
 */
public record Overlap(SubmoduleId submodule1, SubmoduleId submodule2) {

    /**
     * Returns the set of submodule IDs involved in this overlap.
     *
     * @return a set containing both submodule IDs
     */
    public Set<SubmoduleId> submoduleIds() {
        return Set.of(submodule1, submodule2);
    }
}

