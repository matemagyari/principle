package org.tindalos.principle.domain;

import java.util.List;
import java.util.Optional;

import org.tindalos.principle.domain.analyzers.acd.ComponentDependenciesResult;
import org.tindalos.principle.domain.analyzers.adp.ADPResult;
import org.tindalos.principle.domain.analyzers.layering.LayerViolationsResult;
import org.tindalos.principle.domain.analyzers.sap.SAPResult;
import org.tindalos.principle.domain.analyzers.sdp.SDPResult;
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.principle.domain.analyzers.submodulesblueprint.SubmodulesBlueprintAnalysisResult;
import org.tindalos.principle.domain.analyzers.thirdparty.ThirdPartyViolationsResult;

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
        return firstResultOfType(ADPResult.class);
    }

    public Optional<LayerViolationsResult> layerViolationsResult() {
        return firstResultOfType(LayerViolationsResult.class);
    }

    public Optional<ThirdPartyViolationsResult> thirdPartyViolationsResult() {
        return firstResultOfType(ThirdPartyViolationsResult.class);
    }

    public Optional<SDPResult> sdpResult() {
        return firstResultOfType(SDPResult.class);
    }

    public Optional<SAPResult> sapResult() {
        return firstResultOfType(SAPResult.class);
    }

    public Optional<ComponentDependenciesResult> componentDependenciesResult() {
        return firstResultOfType(ComponentDependenciesResult.class);
    }

    public Optional<SubmodulesBlueprintAnalysisResult> submodulesBlueprintAnalysisResult() {
        return firstResultOfType(SubmodulesBlueprintAnalysisResult.class);
    }

    public Optional<CohesionAnalysisResult> cohesionAnalysisResult() {
        return firstResultOfType(CohesionAnalysisResult.class);
    }

    private <T extends AnalysisResult> Optional<T> firstResultOfType(Class<T> resultType) {
        return results.stream()
                .filter(resultType::isInstance)
                .map(resultType::cast)
                .findFirst();
    }
}