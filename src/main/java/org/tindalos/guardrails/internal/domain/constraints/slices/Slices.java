package org.tindalos.guardrails.internal.domain.constraints.slices;

import java.util.List;

import org.tindalos.guardrails.internal.domain.core.Constraint;

/**
 * Top-level constraint representing all slice groups configured in Guardrails.
 */
public record Slices(List<SliceGroup> sliceGroups) implements Constraint {
    public Slices {
        sliceGroups = List.copyOf(sliceGroups);
    }
}
