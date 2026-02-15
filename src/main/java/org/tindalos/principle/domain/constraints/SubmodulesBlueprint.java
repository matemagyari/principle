package org.tindalos.principle.domain.constraints;

/**
 * Represents a blueprint configuration for submodule structure validation.
 * Defines the location of a YAML blueprint file that specifies expected module dependencies
 * and the maximum number of violations allowed.
 *
 * @param location path to the blueprint YAML file
 * @param violationThreshold maximum number of violations allowed before failing
 */
public record SubmodulesBlueprint(String location, int violationThreshold) implements IntExpectation {

    public static SubmodulesBlueprint of(String location) {
        return new SubmodulesBlueprint(location, 0);
    }
}

