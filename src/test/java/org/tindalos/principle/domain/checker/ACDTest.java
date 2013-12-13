package org.tindalos.principle.domain.checker;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.coredetector.CheckResult;
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
    public void simple() {
        
        List<LayerReference> result = run("org.tindalos.principletest.layering.simple");
        
        Set<LayerReference> expected = Sets.newHashSet(
                new LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.app")
                ,new LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.infrastructure")
                ,new LayerReference("org.tindalos.principletest.layering.simple.app", "org.tindalos.principletest.layering.simple.infrastructure")
                );
        assertEquals(expected, Sets.newHashSet(result));
    }
    
    private List<LayerReference> run(String basePackage) {
        init(basePackage);
        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(designQualityCheckConfiguration);
        List<CheckResult> resultList = checkResults.resultList();
        assertEquals(1, resultList.size());
        LayerViolationsResult adpResult = (LayerViolationsResult) resultList.get(0);
        return adpResult.getViolations();
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
