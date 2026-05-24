package org.tindalos.guardrails.internal.domain.constraints.slices;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains slice definitions for a single logical grouping (e.g. Layers, Vertical Slices),
 * along with a local violation threshold.
 */
public record SliceGroup(
        String name,
        Map<SliceId, SliceDefinition> slices,
        int violationThreshold) {

    public SliceGroup {
        slices = java.util.Collections.unmodifiableMap(new java.util.LinkedHashMap<>(slices));
    }

    /**
     * Validates that no two slice definitions in this group overlap.
     */
    public void checkNoOverlaps() {
        List<SliceDefinition> definitionList = new ArrayList<>(slices.values());
        Set<SliceOverlap> overlaps = new HashSet<>();

        for (SliceDefinition sliceDefinition : definitionList) {
            for (SliceDefinition anOtherDefinition : definitionList) {
                if (!sliceDefinition.equals(anOtherDefinition)
                        && sliceDefinition.overlapsWith(anOtherDefinition)) {
                    overlaps.add(new SliceOverlap(sliceDefinition.id(), anOtherDefinition.id()));
                }
            }
        }

        if (!overlaps.isEmpty()) {
            throw new OverlappingSliceDefinitionsException(overlaps);
        }
    }
}
