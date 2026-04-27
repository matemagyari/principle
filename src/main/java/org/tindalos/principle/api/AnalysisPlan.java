package org.tindalos.principle.api;

import java.util.Objects;

/**
 * Opaque public analysis plan that can be executed by the Principle API.
 */
public final class AnalysisPlan {

    private final org.tindalos.principle.internal.domain.plan.AnalysisPlan delegate;

    AnalysisPlan(org.tindalos.principle.internal.domain.plan.AnalysisPlan delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    org.tindalos.principle.internal.domain.plan.AnalysisPlan toInternalPlan() {
        return delegate;
    }

    public String basePackage() {
        return delegate.basePackage();
    }
}
