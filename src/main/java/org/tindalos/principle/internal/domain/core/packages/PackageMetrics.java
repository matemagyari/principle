package org.tindalos.principle.internal.domain.core.packages;

/**
 * Represents architectural metrics for a package.
 * Includes coupling, abstractness, instability, and distance from the main sequence.
 *
 * @param afferentCoupling number of packages that depend on this package
 * @param efferentCoupling number of packages this package depends on
 * @param abstractness ratio of abstract classes/interfaces to total classes (0-1)
 * @param instability ratio of efferent to total coupling (0-1), measures resistance to change
 * @param distance distance from the main sequence, indicates balance between abstractness and stability
 */
public record PackageMetrics(
        int afferentCoupling,
        int efferentCoupling,
        float abstractness,
        float instability,
        float distance) {

    /**
     * Undefined/uninitialized metrics instance.
     * Used as a placeholder when metrics haven't been calculated yet.
     */
    public static final PackageMetrics UNDEFINED = new PackageMetrics(0, 0, 0, 0, 0);

    /**
     * Checks if metrics have been calculated.
     *
     * @return true if metrics are calculated, false if this is the UNDEFINED instance
     */
    public boolean isCalculated() {
        return this != UNDEFINED;
    }

    public boolean isIsolated() {
        return afferentCoupling == 0 && efferentCoupling == 0;
    }

}

