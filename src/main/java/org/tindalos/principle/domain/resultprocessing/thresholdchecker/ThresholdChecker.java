package org.tindalos.principle.domain.resultprocessing.thresholdchecker;

import org.tindalos.principle.domain.checker.DesignQualityCheckResults;
import org.tindalos.principle.domain.checkerparameter.Checks;
import org.tindalos.principle.domain.checkerparameter.PackageCoupling;
import org.tindalos.principle.domain.detector.adp.APDResult;
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult;
import org.tindalos.principle.domain.detector.sap.SAPResult;
import org.tindalos.principle.domain.detector.sdp.SDPResult;

import com.google.common.base.Optional;

public class ThresholdChecker {
	
    public void trespassed(DesignQualityCheckResults checkResults, Checks checks) {

        Optional<APDResult> apdResult = checkResults.getResult(APDResult.class);
        Optional<LayerViolationsResult> layerViolationsResult = checkResults.getResult(LayerViolationsResult.class);
        Optional<SDPResult> sdpResult = checkResults.getResult(SDPResult.class);
        Optional<SAPResult> sapResult = checkResults.getResult(SAPResult.class);

        PackageCoupling packageCoupling = checks.getPackageCoupling();
        boolean thresholdViolated =  
        			packageCoupling.getADP().getViolationsThreshold() < apdResult.get().numberOfViolations()
        			|| packageCoupling.getSDP().getViolationsThreshold() < sdpResult.get().numberOfViolations()
        			|| packageCoupling.getSAP().getViolationsThreshold() < sapResult.get().numberOfViolations()
        		 	|| checks.getLayering().getViolationsThreshold() < layerViolationsResult.get().numberOfViolations();
        
        if (thresholdViolated) {
        	throw new ThresholdTrespassedException();
        }
        		
    }

}
