package org.tindalos.principle.domain.analyzers.sdp;

import java.util.ArrayList;
import java.util.HashMap;

import org.tindalos.principle.domain.AnalysisInput;
import org.tindalos.principle.domain.analyzers.Analyzer;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.core.packages.PackageReference;
import org.tindalos.principle.domain.core.packages.PackageWithMetrics;

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
            for (var reference : aPackage.getOwnPackageReferences()) {
                var referencedPackage = references.get(reference);
                if (referencedPackage != null
                        && referencedPackage.getMetrics().instability() > aPackage.getMetrics().instability()) {
                    sdpViolations.add(new SDPViolation(aPackage, referencedPackage));
                }
            }
        }

        return new SDPResult(sdpViolations, checkInput.packageCouplingExpectations().flatMap(pc -> pc.sdp()).get());
    }

    @Override
    public boolean isEnabled(Constraints expectations) {
        return expectations.packageCoupling().isPresent() && expectations.packageCoupling().get().sdp().isPresent();
    }
}