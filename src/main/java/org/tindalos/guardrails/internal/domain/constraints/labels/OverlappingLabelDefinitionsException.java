package org.tindalos.guardrails.internal.domain.constraints.labels;

import java.util.Set;

/**
 * Exception thrown when label definitions overlap.
 */
public class OverlappingLabelDefinitionsException extends InvalidLabelDefinitionException {

    private final Set<LabelOverlap> overlaps;

    public OverlappingLabelDefinitionsException(Set<LabelOverlap> overlaps) {
        super(toMessage(overlaps));
        this.overlaps = Set.copyOf(overlaps);
    }

    public Set<LabelOverlap> getOverlaps() {
        return overlaps;
    }

    private static String toMessage(Set<LabelOverlap> overlaps) {
        StringBuilder msg = new StringBuilder("Overlapping labels detected: ");
        for (LabelOverlap overlap : overlaps) {
            msg.append("\n").append(overlap.first().value()).append(" and ").append(overlap.second().value());
        }
        return msg.toString();
    }
}
