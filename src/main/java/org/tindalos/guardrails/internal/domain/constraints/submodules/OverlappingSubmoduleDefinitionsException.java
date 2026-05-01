package org.tindalos.guardrails.internal.domain.constraints.submodules;

import java.util.Set;

/**
 * Exception thrown when submodule definitions overlap.
 * Overlapping submodules occur when the same package is defined in multiple submodules.
 */
public class OverlappingSubmoduleDefinitionsException extends InvalidBlueprintDefinitionException {

    private final Set<Overlap> overlaps;

    public OverlappingSubmoduleDefinitionsException(Set<Overlap> overlaps) {
        super(toMessage(overlaps));
        this.overlaps = overlaps;
    }

    public Set<Overlap> getOverlaps() {
        return overlaps;
    }

    private static String toMessage(Set<Overlap> overlaps) {
        StringBuilder msg = new StringBuilder("Overlapping submodules: ");

        for (Overlap overlap : overlaps) {
            msg.append("\n");
            Set<SubmoduleId> ids = overlap.submoduleIds();
            for (SubmoduleId submoduleId : ids) {
                msg.append(submoduleId).append(" and ");
            }
            // Remove the trailing " and " by going back 4 characters from the last append
            int currentLength = msg.length();
            if (currentLength > 0) {
                msg.delete(currentLength - 5, currentLength);
            }
        }

        return msg.toString();
    }

}

