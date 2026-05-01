package org.tindalos.guardrails.api;

import java.util.Objects;

/**
 * Opaque public analysis plan that can be executed by the Guardrails API.
 */
public final class AnalysisPlan {

    private final org.tindalos.guardrails.internal.domain.plan.AnalysisPlan delegate;

    AnalysisPlan(org.tindalos.guardrails.internal.domain.plan.AnalysisPlan delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    org.tindalos.guardrails.internal.domain.plan.AnalysisPlan toInternalPlan() {
        return delegate;
    }

    public String basePackage() {
        return delegate.basePackage();
    }
}
