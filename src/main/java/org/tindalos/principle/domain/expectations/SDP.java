package org.tindalos.principle.domain.expectations;

/**
 * Stable Dependencies Principle (SDP) threshold expectation.
 *
 * <p>SDP states that dependencies should flow in the direction of stability. A package should only
 * depend on packages that are more stable than itself. This prevents fragile packages from becoming
 * a foundation that stable packages rely upon.</p>
 *
 * <p><b>Stability</b> is measured by the ratio of efferent (outgoing) dependencies to total dependencies.
 * A package is considered stable if it has many incoming dependencies but few outgoing dependencies,
 * because changes to stable packages affect many other packages.</p>
 *
 * <p>Key metrics:</p>
 * <ul>
 *   <li><b>Afferent Coupling (Ca)</b>: Number of packages that depend on this package</li>
 *   <li><b>Efferent Coupling (Ce)</b>: Number of packages this package depends on</li>
 *   <li><b>Instability (I)</b>: I = Ce / (Ce + Ca), ranging from 0 (stable) to 1 (unstable)</li>
 * </ul>
 *
 * <p>Violations occur when a package depends on a less stable (more unstable) package, creating
 * potential maintenance issues and making the system harder to change safely.</p>
 *
 * <p>The threshold represents the maximum number of such violations allowed.</p>
 *
 * @param violationThreshold the maximum number of SDP violations allowed (default is 0)
 */
public record SDP(int violationThreshold) implements IntThresholder {

    public SDP() {
        this(0);
    }
}

