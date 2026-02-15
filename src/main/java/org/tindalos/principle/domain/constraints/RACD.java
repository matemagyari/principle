package org.tindalos.principle.domain.constraints;

/**
 * Relative Average Component Dependency (RACD) threshold expectation.
 *
 * <p>RACD is the normalized version of ACD (Average Component Dependency), calculated as:
 * RACD = ACD / Number_of_Components</p>
 *
 * <p>This metric provides a relative measure of internal coupling that is independent of
 * package size, making it easier to compare coupling across packages of different sizes.
 * A lower RACD indicates better modularity with less relative internal coupling.</p>
 *
 * <p>RACD values typically range from 0.0 (no internal dependencies) to 1.0 (high internal coupling),
 * making it useful for setting consistent thresholds across different package structures.</p>
 *
 * @param threshold the maximum allowed RACD value (default is 0.0)
 */
public record RACD(double threshold) implements ComponentDependencyConstraint {
    public RACD() {
        this(0.0);
    }
}

