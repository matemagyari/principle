package org.tindalos.guardrails.internal.domain.constraints.slices;

import java.util.Set;

/**
 * Exception thrown when slice definitions overlap.
 */
public class OverlappingSliceDefinitionsException extends InvalidSliceDefinitionException {

    private final Set<SliceOverlap> overlaps;

    public OverlappingSliceDefinitionsException(Set<SliceOverlap> overlaps) {
        super(toMessage(overlaps));
        this.overlaps = Set.copyOf(overlaps);
    }

    public Set<SliceOverlap> getOverlaps() {
        return overlaps;
    }

    private static String toMessage(Set<SliceOverlap> overlaps) {
        StringBuilder msg = new StringBuilder("Overlapping slices detected: ");
        for (SliceOverlap overlap : overlaps) {
            msg.append("\n").append(overlap.first().value()).append(" and ").append(overlap.second().value());
        }
        return msg.toString();
    }
}
