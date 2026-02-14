package org.tindalos.principle.domain.expectations;

/**
 * Average Component Dependency (ACD) threshold expectation.
 *
 * <p>ACD measures the average number of internal dependencies (classes within the same package)
 * that components depend on. A lower ACD indicates better modularity and loose coupling within
 * a package structure.</p>
 *
 * <p>This metric helps identify packages that have high internal coupling, which can make
 * the codebase harder to maintain, test, and understand.</p>
 *
 * @param threshold the maximum allowed ACD value (default is 0.0)
 */
public record ACD(double threshold) implements ComponentDependency {
    public ACD() {
        this(0.0);
    }
}

