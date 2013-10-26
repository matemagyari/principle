package org.tindalos.principle.domain.detector.sdp;

import java.util.List;
import java.util.Map;

import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.detector.adp.PackageStructureBuilder;
import org.tindalos.principle.domain.detector.core.CheckInput;
import org.tindalos.principle.domain.detector.core.CheckResult;
import org.tindalos.principle.domain.detector.core.Detector;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class SDPViolationDetector implements Detector {

	public CheckResult analyze(CheckInput checkInput) {
        
		List<Package> packages = checkInput.getPackages();
		List<SDPViolation> sdpViolations = Lists.newArrayList();
        Map<PackageReference, Package> references = buildReferenceMap(packages);
        for (Package aPackage : packages) {
			
        	for (Package aReferredPackage : referredPackages(aPackage, references)) {
        		System.err.println( aPackage + "["+aPackage.instability()+"] --> " + aReferredPackage + "["+aReferredPackage.instability()+"]");
				if (aReferredPackage.instability() > aPackage.instability()) {
					sdpViolations.add(new SDPViolation(aPackage, aReferredPackage));
				}
			}
		}
		return new SDPResult(sdpViolations);
	}

	private Map<PackageReference, Package> buildReferenceMap(List<Package> packages) {
		Map<PackageReference, Package> result = Maps.newHashMap();
		for (Package aPackage : packages) {
			result.put(aPackage.getReference(), aPackage);
		}
		return result;
	}

	private List<Package> referredPackages(Package aPackage, Map<PackageReference, Package> references) {
		List<Package> results = Lists.newArrayList();
		for (PackageReference reference : aPackage.getOwnPackageReferences()) {
			results.add(references.get(reference));
		}
		return results;
	}

}
