package org.tindalos.principle.domain.expectations;

import java.util.Collections;
import java.util.List;

/**
 * Represents third-party dependency restrictions for architectural layers.
 * Defines which external libraries are allowed in specific layers through barriers.
 * Used to enforce architectural boundaries and prevent unwanted external dependencies.
 *
 * @param barriers list of barriers defining allowed libraries per layer
 * @param violationThreshold maximum number of violations allowed before failing
 */
public record ThirdParty(List<Barrier> barriers, int violationThreshold) implements IntExpectation {

    public ThirdParty {
        barriers = Collections.unmodifiableList(barriers);
    }

}

