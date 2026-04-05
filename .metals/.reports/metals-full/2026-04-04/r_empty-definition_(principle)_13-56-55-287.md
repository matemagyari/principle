error id: file://<WORKSPACE>/src/main/java/org/tindalos/principle/domain/analyzers/submodulesblueprint/OverlappingSubmoduleDefinitionsException.java:local3
file://<WORKSPACE>/src/main/java/org/tindalos/principle/domain/analyzers/submodulesblueprint/OverlappingSubmoduleDefinitionsException.java
empty definition using pc, found symbol in pc: 
found definition using semanticdb; symbol local3
empty definition using fallback
non-local guesses:

offset: 821
uri: file://<WORKSPACE>/src/main/java/org/tindalos/principle/domain/analyzers/submodulesblueprint/OverlappingSubmoduleDefinitionsException.java
text:
```scala
package org.tindalos.principle.domain.analyzers.submodulesblueprint;

import scala.collection.JavaConverters;

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

        for (Overlap overlap@@ : overlaps) {
            msg.append("\n");
            Set<SubmoduleId> scalaSet = overlap.submoduleIds();
            for (SubmoduleId submoduleId : scalaSet) {
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


```


#### Short summary: 

empty definition using pc, found symbol in pc: 