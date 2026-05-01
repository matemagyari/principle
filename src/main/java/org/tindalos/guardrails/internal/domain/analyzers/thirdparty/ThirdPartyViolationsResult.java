package org.tindalos.guardrails.internal.domain.analyzers.thirdparty;

import org.tindalos.guardrails.internal.domain.core.AnalysisResult;
import org.tindalos.guardrails.internal.domain.constraints.ThirdParty;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;

import java.util.Map;
import java.util.Set;

/**
 * Represents the result of a Third Party dependency analysis.
 * Contains violations grouped by the referring package, mapping each referrer
 * to the set of disallowed third-party packages it depends on.
 *
 * @param violations           map of referrer package to set of disallowed dependencies
 * @param thirdPartyExpectations the third-party constraint configuration
 */
public record ThirdPartyViolationsResult(
        Map<PackageReference, Set<PackageReference>> violations,
        ThirdParty thirdPartyExpectations) implements AnalysisResult {

    public int threshold() {
        return thirdPartyExpectations.violationThreshold();
    }

    @Override
    public boolean constraintViolated() {
        return violations.values().stream().mapToInt(Set::size).sum() > threshold();
    }
}

