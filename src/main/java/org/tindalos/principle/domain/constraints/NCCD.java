package org.tindalos.principle.domain.constraints;

/**
 * Normalized Cumulative Component Dependency (NCCD) threshold expectation.
 *
 * <p>NCCD is another normalized measure of package coupling, calculated as:
 * NCCD = ACD / Number_of_Components</p>
 *
 * <p>This metric represents the cumulative internal dependencies normalized by package size,
 * providing insight into how tightly components within a package are interconnected relative
 * to the package's overall size.</p>
 *
 * <p>Like RACD, NCCD provides a size-independent measure of coupling, making it useful for
 * comparing architectural quality across packages of varying sizes. Lower NCCD values indicate
 * better separation of concerns and looser coupling within the package structure.</p>
 *
 * @param threshold the maximum allowed NCCD value (default is 0.0)
 */
public record NCCD(double threshold) implements ComponentDependencyConstraint {
    public NCCD() {
        this(0.0);
    }
}

