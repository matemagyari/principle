package org.tindalos.principle.domain.constraints;

/**
 * Stable Abstractions Principle (SAP) threshold expectation.
 *
 * <p>SAP states that stable packages (those that many other packages depend on) should be abstract,
 * while unstable packages (those with few dependents) can be concrete. This principle creates a
 * balance between stability and flexibility in the codebase.</p>
 *
 * <p>The principle is measured using the relationship between:</p>
 * <ul>
 *   <li><b>Abstractness (A)</b>: Ratio of abstract classes/interfaces to total classes in a package</li>
 *   <li><b>Instability (I)</b>: Ratio of efferent (outgoing) to total dependencies</li>
 * </ul>
 *
 * <p>Ideally, packages should lie near the "main sequence" where A + I ≈ 1. Packages far from this
 * line are either too abstract with few dependents (useless), or too concrete with many dependents
 * (painful to change).</p>
 *
 * <p>The distance from the main sequence is calculated as: D = |A + I - 1|. Packages with distance
 * greater than the maxDistance threshold are considered violations.</p>
 *
 * @param violationThreshold the maximum number of SAP violations allowed (default is 0)
 * @param maxDistance the maximum allowed distance from the main sequence (default is 0.0).
 *                    A package with distance D > maxDistance is considered a violation.
 *                    Typical values range from 0.0 (strict) to 0.3 (more lenient).
 */
public record SAP(int violationThreshold, double maxDistance) implements IntConstraint {
    public SAP() {
        this(0, 0.0);
    }
}

