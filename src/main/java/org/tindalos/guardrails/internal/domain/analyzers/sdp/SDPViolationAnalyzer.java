package org.tindalos.guardrails.internal.domain.analyzers.sdp;

import java.util.ArrayList;
import java.util.HashMap;

import org.tindalos.guardrails.internal.domain.plan.AnalysisInput;
import org.tindalos.guardrails.internal.domain.analyzers.Analyzer;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;
import org.tindalos.guardrails.internal.domain.core.packages.PackageWithMetrics;

/**
 * Detects Stable Dependencies Principle violations.
 */
public class SDPViolationAnalyzer implements Analyzer {

    @Override
    public SDPResult analyze(AnalysisInput checkInput) {
        var packages = checkInput.packages();
        var references = new HashMap<PackageReference, PackageWithMetrics>();
        for (var aPackage : packages) {
            references.put(aPackage.reference(), aPackage);
        }

        var sdpViolations = new ArrayList<SDPViolation>();
        for (var aPackage : packages) {
            for (var reference : aPackage.ownPackageReferences()) {
                var referencedPackage = references.get(reference);
                if (referencedPackage != null
                        && referencedPackage.metrics().instability() > aPackage.metrics().instability()) {
                    sdpViolations.add(new SDPViolation(aPackage, referencedPackage));
                }
            }
        }

        return new SDPResult(sdpViolations, checkInput
            .packageCouplingExpectations()
            .flatMap(pc -> pc.sdp())
            .orElseThrow());
    }

    @Override
    public boolean isEnabled(Constraints constraints) {
        return constraints.packageCoupling().flatMap(pc -> pc.sdp()).isPresent();
    }
}