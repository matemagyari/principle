package org.tindalos.guardrails.internal.domain;

import java.util.List;
import java.util.Optional;

import org.tindalos.guardrails.internal.domain.analyzers.acd.ComponentDependenciesResult;
import org.tindalos.guardrails.internal.domain.analyzers.adp.ADPResult;
import org.tindalos.guardrails.internal.domain.analyzers.labels.LabelsAnalysisResult;
import org.tindalos.guardrails.internal.domain.analyzers.sap.SAPResult;
import org.tindalos.guardrails.internal.domain.analyzers.sdp.SDPResult;
import org.tindalos.guardrails.internal.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.guardrails.internal.domain.analyzers.thirdparty.ThirdPartyViolationsResult;
import org.tindalos.guardrails.internal.domain.core.AnalysisResult;

/**
 * Immutable aggregate wrapper around analysis results.
 * Provides typed accessors for known analysis result subtypes.
 */
public final class AggregatedAnalysisResults {

    private final List<AnalysisResult> results;

    public AggregatedAnalysisResults(List<AnalysisResult> results) {
        this.results = List.copyOf(results);
    }

    public List<AnalysisResult> results() {
        return results;
    }

    public boolean hasViolations() {
        return results.stream().anyMatch(AnalysisResult::constraintViolated);
    }

    public Optional<ADPResult> adpResult() {
        return resultOfType(ADPResult.class);
    }

    public Optional<ThirdPartyViolationsResult> thirdPartyViolationsResult() {
        return resultOfType(ThirdPartyViolationsResult.class);
    }

    public Optional<SDPResult> sdpResult() {
        return resultOfType(SDPResult.class);
    }

    public Optional<SAPResult> sapResult() {
        return resultOfType(SAPResult.class);
    }

    public Optional<ComponentDependenciesResult> componentDependenciesResult() {
        return resultOfType(ComponentDependenciesResult.class);
    }

    public Optional<LabelsAnalysisResult> labelsAnalysisResult() {
        return resultOfType(LabelsAnalysisResult.class);
    }

    public Optional<CohesionAnalysisResult> cohesionAnalysisResult() {
        return resultOfType(CohesionAnalysisResult.class);
    }

    public <T extends AnalysisResult> Optional<T> resultOfType(Class<T> resultType) {
        return results.stream()
                .filter(resultType::isInstance)
                .map(resultType::cast)
                .findFirst();
    }
}