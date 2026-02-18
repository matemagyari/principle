package org.tindalos.principle.domain.analyzers.submodulesblueprint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains all module definitions in a blueprint and validates that
 * no modules overlap (i.e., no two modules contain packages that are
 * subpackages of each other).
 */
public class SubmoduleDefinitions {

    private final Map<SubmoduleId, SubmoduleDefinition> definitions;

    public SubmoduleDefinitions(Map<SubmoduleId, SubmoduleDefinition> definitions) {
        this.definitions = Map.copyOf(definitions);
        checkNoOverlaps(new ArrayList<>(definitions.values()));
    }

    public Map<SubmoduleId, SubmoduleDefinition> getDefinitions() {
        return definitions;
    }

    private void checkNoOverlaps(List<SubmoduleDefinition> definitionList) {
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

