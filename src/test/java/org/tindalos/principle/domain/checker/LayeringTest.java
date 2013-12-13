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
import org.tindalos.principle.domain.expectations.Layering;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.plugin.Checks;

import com.google.common.collect.Sets;

public class LayeringTest {

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

    private static DesignQualityExpectations prepareChecks() {
        Checks checks = new Checks();
        checks.setLayering(layering());
        return checks;
    }

    private static Layering layering() {
        Layering layering = new Layering();
        layering.setLayers("infrastructure","app","domain");
        return layering;
    }


}
