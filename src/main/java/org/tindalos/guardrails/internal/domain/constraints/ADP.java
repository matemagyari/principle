package org.tindalos.guardrails.internal.domain.constraints;

/**
 * Acyclic Dependency Principle (ADP) threshold expectation.
 *
 * <p>ADP enforces that there should be no cyclic dependencies between packages.
 * Cyclic dependencies make code harder to understand, test, and maintain, as changes
 * in one package can have cascading effects through the cycle.</p>
 *
 * <p>This guardrails is fundamental to good software architecture. A well-structured
 * codebase should have packages organized in a Directed Acyclic Graph (DAG), where
 * dependencies flow in one direction.</p>
 *
 * <p><b>Problems caused by cyclic dependencies:</b></p>
 * <ul>
 *   <li>Difficult to understand the overall structure</li>
 *   <li>Hard to test packages in isolation</li>
 *   <li>Changes ripple through the cycle unexpectedly</li>
 *   <li>Impossible to release packages independently</li>
 *   <li>Build order becomes ambiguous</li>
 * </ul>
 *
 * <p>The threshold represents the maximum number of cycles allowed before the check fails.
 * Typically, this should be set to 0 (no cycles allowed).</p>
 *
 * @param violationThreshold the maximum number of cycles allowed (default is 0)
 */
public record ADP(int violationThreshold) implements IntConstraint {

    /**
     * Creates an ADP expectation with default threshold of 0 (no cycles allowed).
     */
    public ADP() {
        this(0);
    }
}

