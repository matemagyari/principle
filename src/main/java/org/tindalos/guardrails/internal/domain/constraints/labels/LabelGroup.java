package org.tindalos.guardrails.internal.domain.constraints.labels;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains label definitions for a single logical grouping (e.g. Layers, Vertical Slices/Modularization),
 * along with a local violation threshold.
 */
public record LabelGroup(
        String name,
        Map<LabelId, LabelDefinition> labels,
        int violationThreshold) {

    public LabelGroup {
        labels = java.util.Collections.unmodifiableMap(new java.util.LinkedHashMap<>(labels));
    }

    /**
     * Validates that no two label definitions in this group overlap.
     */
    public void checkNoOverlaps() {
        List<LabelDefinition> definitionList = new ArrayList<>(labels.values());
        Set<LabelOverlap> overlaps = new HashSet<>();

        for (LabelDefinition labelDefinition : definitionList) {
            for (LabelDefinition anOtherDefinition : definitionList) {
                if (!labelDefinition.equals(anOtherDefinition)
                        && labelDefinition.overlapsWith(anOtherDefinition)) {
                    overlaps.add(new LabelOverlap(labelDefinition.id(), anOtherDefinition.id()));
                }
            }
        }

        if (!overlaps.isEmpty()) {
            throw new OverlappingLabelDefinitionsException(overlaps);
        }
    }
}
