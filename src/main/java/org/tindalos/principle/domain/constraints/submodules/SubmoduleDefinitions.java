package org.tindalos.principle.domain.constraints.submodules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains all module definitions in a blueprint, along with a violation
 * threshold. Use {@link #checkNoOverlaps()} to validate that no modules
 * overlap (i.e., no two modules contain packages that are subpackages of each other).
 */
public class SubmoduleDefinitions {

    private final Map<SubmoduleId, SubmoduleDefinition> definitions;
    private final int violationThreshold;

    public SubmoduleDefinitions(Map<SubmoduleId, SubmoduleDefinition> definitions, int violationThreshold) {
        this.definitions = Map.copyOf(definitions);
        this.violationThreshold = violationThreshold;
    }

    public SubmoduleDefinitions(Map<SubmoduleId, SubmoduleDefinition> definitions) {
        this(definitions, 0);
    }

    public Map<SubmoduleId, SubmoduleDefinition> getDefinitions() {
        return definitions;
    }

    public int violationThreshold() {
        return violationThreshold;
    }

    /**
     * Validates that no two module definitions overlap, throwing
     * {@link OverlappingSubmoduleDefinitionsException} if any do.
     */
    public void checkNoOverlaps() {
        List<SubmoduleDefinition> definitionList = new ArrayList<>(definitions.values());
        Set<Overlap> overlaps = new HashSet<>();

        for (SubmoduleDefinition submoduleDefinition : definitionList) {
            for (SubmoduleDefinition anOtherDefinition : definitionList) {
                if (!submoduleDefinition.equals(anOtherDefinition)
                    && submoduleDefinition.overlapsWith(anOtherDefinition)) {
                    overlaps.add(new Overlap(submoduleDefinition.id(), anOtherDefinition.id()));
                }
            }
        }

        if (!overlaps.isEmpty()) {
            throw new OverlappingSubmoduleDefinitionsException(overlaps);
        }
    }
}

