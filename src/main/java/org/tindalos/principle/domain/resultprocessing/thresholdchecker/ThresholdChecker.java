package org.tindalos.principle.domain.resultprocessing.thresholdchecker;

import org.tindalos.principle.domain.checker.DesignQualityCheckResults;
import org.tindalos.principle.domain.core.checkerparameter.Checks;
import org.tindalos.principle.domain.core.checkerparameter.PackageCoupling;
import org.tindalos.principle.domain.core.checkerparameter.cumulativedependency.DoubleThresholders;
import org.tindalos.principle.domain.detector.acd.ACDResult;
import org.tindalos.principle.domain.detector.adp.APDResult;
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult;
import org.tindalos.principle.domain.detector.sap.SAPResult;
import org.tindalos.principle.domain.detector.sdp.SDPResult;

import com.google.common.base.Optional;

public class ThresholdChecker {
	
    public void trespassed(DesignQualityCheckResults checkResults, Checks checks) {

    	Optional<LayerViolationsResult> layerViolationsResult = checkResults.getResult(LayerViolationsResult.class);
        Optional<APDResult> apdResult = checkResults.getResult(APDResult.class);
        Optional<SDPResult> sdpResult = checkResults.getResult(SDPResult.class);
        Optional<SAPResult> sapResult = checkResults.getResult(SAPResult.class);
        Optional<ACDResult> acdResult = checkResults.getResult(ACDResult.class);

        PackageCoupling packageCoupling = checks.getPackageCoupling();
        
        boolean cdThresholdsViolated = violates(packageCoupling.getACD(), acdResult.get().getACD())
        		|| violates(packageCoupling.getRACD(), acdResult.get().getRACD())
        		|| violates(packageCoupling.getNCCD(), acdResult.get().getNCCD());
        
        boolean thresholdViolated =  
        			packageCoupling.getADP().getViolationsThreshold() < apdResult.get().numberOfViolations()
        			|| packageCoupling.getSDP().getViolationsThreshold() < sdpResult.get().numberOfViolations()
        			|| packageCoupling.getSAP().getViolationsThreshold() < sapResult.get().numberOfViolations()
        			|| cdThresholdsViolated
        		 	|| checks.getLayering().getViolationsThreshold() < layerViolationsResult.get().numberOfViolations();
        
        if (thresholdViolated) {
        	throw new ThresholdTrespassedException();
        }
        		
    }

	private boolean violates(DoubleThresholders threshold, Double value) {
		if (threshold == null || threshold.getThreshold() == null) {
			return false;
		}
		return threshold.getThreshold() < value;
	}

}
