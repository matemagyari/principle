package org.tindalos.guardrails.internal.domain.constraints.labels;

/**
 * Represents a unique identifier for a label within a group.
 */
public record LabelId(String value) {
    public LabelId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Label ID value cannot be null or blank");
        }
    }
}
