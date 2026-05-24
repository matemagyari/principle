package org.tindalos.guardrails.internal.domain.constraints.slices;

/**
 * Represents a unique identifier for a slice within a group.
 */
public record SliceId(String value) {
    public SliceId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Slice ID value cannot be null or blank");
        }
    }
}
