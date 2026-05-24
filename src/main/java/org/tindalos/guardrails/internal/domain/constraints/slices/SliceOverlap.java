package org.tindalos.guardrails.internal.domain.constraints.slices;

/**
 * Represents an overlap between two slice definitions, meaning they
 * contain overlapping package definitions.
 */
public record SliceOverlap(SliceId first, SliceId second) {}
