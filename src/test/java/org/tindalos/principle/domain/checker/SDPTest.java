package org.tindalos.principle.domain.checker;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.detector.sdp.SDPResult;
import org.tindalos.principle.domain.detector.sdp.SDPViolation;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.PackageCoupling;
import org.tindalos.principle.domain.expectations.SDP;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.plugin.Checks;

public class SDPTest {

    private DesignQualityCheckConfiguration designQualityCheckConfiguration;
    private DesignQualityCheckService designQualityCheckService;
    private DesignQualityExpectations checks = prepareChecks();

    @BeforeClass
    public static void setup() {
        TestFixture.setLogger();
    }

    private void init(String basePackage) {
        designQualityCheckService = PoorMansDIContainer.getDesignCheckService(basePackage);
        designQualityCheckConfiguration = new DesignQualityCheckConfiguration(checks, basePackage);
    }

    @Test
    public void simple() {
        
        SDPResult result = run("org.tindalos.principletest.sdp");
        
        List<SDPViolation> violations = result.getViolationsAsJavaList();
        
        for (SDPViolation violation : violations) {
            System.err.println(violation);
        }
    }
    
    private SDPResult run(String basePackage) {
        init(basePackage);
        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(designQualityCheckConfiguration);
        List<CheckResult> resultList = checkResults.resultList();
        assertEquals(1, resultList.size());
        return (SDPResult) resultList.get(0);
    }

    private DesignQualityExpectations prepareChecks() {
        Checks checks = new Checks();
        checks.setPackageCoupling(packageCoupling());
        return checks;
    }
    
    private PackageCoupling packageCoupling() {
        PackageCoupling packageCoupling = new PackageCoupling();
        packageCoupling.setSDP(new SDP());
        return packageCoupling;
    }

}
