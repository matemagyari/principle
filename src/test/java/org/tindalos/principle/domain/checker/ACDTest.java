package org.tindalos.principle.domain.checker;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.detector.acd.ACDResult;
import org.tindalos.principle.domain.detector.layering.LayerReference;
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.PackageCoupling;
import org.tindalos.principle.domain.expectations.cumulativedependency.ACD;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.plugin.Checks;

import com.google.common.collect.Sets;

public class ACDTest {

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
    public void simple1() {
        
        Double result = run("org.tindalos.principletest.acd.simple1");
        
        assertEquals(1, result,0.01);
    }
    

    @Test
    public void simple11() {
        
        Double result = run("org.tindalos.principletest.acd.simple11");
        
        assertEquals(1.5, result,0.01);
    }
    
    @Test
    public void simple() {
        
        Double result = run("org.tindalos.principletest.acd.simple");
        
        assertEquals(2.5, result,0.01);
    }
    
    @Test
    public void cyclic3() {
        
        Double result = run("org.tindalos.principletest.acd.cyclic3");
        
        assertEquals(3, result,0.01);
    }
    
    @Test
    public void cyclic6() {
        
        Double result = run("org.tindalos.principletest.acd.cyclic6");
        
        assertEquals(4.33, result,0.01);
    }
    
    private Double run(String basePackage) {
        init(basePackage);
        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(designQualityCheckConfiguration);
        List<CheckResult> resultList = checkResults.resultList();
        assertEquals(1, resultList.size());
        ACDResult result = (ACDResult) resultList.get(0);
        return result.getACD();
    }

    private DesignQualityExpectations prepareChecks() {
        Checks checks = new Checks();
        checks.setPackageCoupling(packageCoupling());
        return checks;
    }
    
    private PackageCoupling packageCoupling() {
        PackageCoupling packageCoupling = new PackageCoupling();
        packageCoupling.setACD(new ACD());
        return packageCoupling;
    }

}
