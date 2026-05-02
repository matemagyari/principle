package org.tindalos.guardrails.api;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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

    public <T extends AnalysisResult> Optional<T> customDefinition(String key, Class<T> type) {
        return Optional.ofNullable(delegate.customDefinitions().get(key))
                .filter(type::isInstance)
                .map(type::cast);
    }

    public Map<String, AnalysisResult> customDefinitions() {
        return delegate.customDefinitions().entrySet().stream()
                .filter(e -> e.getValue() instanceof AnalysisResult)
                .collect(java.util.stream.Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        e -> (AnalysisResult) e.getValue()));
    }
}
