package org.tindalos.guardrails.internal.domain.constraints.labels;

/**
 * Represents an overlap between two label definitions, meaning they
 * contain overlapping package definitions.
 */
public record LabelOverlap(LabelId first, LabelId second) {}
