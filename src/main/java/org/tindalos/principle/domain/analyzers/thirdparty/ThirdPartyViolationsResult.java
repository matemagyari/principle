package org.tindalos.principle.domain.analyzers.thirdparty;

import org.tindalos.principle.domain.AnalysisResult;
import org.tindalos.principle.domain.constraints.ThirdParty;
import org.tindalos.principle.domain.core.packages.PackageReference;

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

