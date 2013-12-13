package org.tindalos.principle.domain.checker;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.CyclesInSubgraph;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.detector.adp.ADPResult;
import org.tindalos.principle.domain.expectations.ADP;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.PackageCoupling;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.plugin.Checks;

public class CycleTest {

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
        
        List<Cycle> cycles = run("org.tindalos.principletest.cycle.simple");
        
        Cycle expectedCycle = new Cycle(new PackageReference("org.tindalos.principletest.cycle.simple.left"),new PackageReference("org.tindalos.principletest.cycle.simple.right"));
        assertEquals(expectedCycle, cycles.get(0));
        assertEquals(1, cycles.size());
    }
    
    @Test
    public void transitive() {
        
        
        List<Cycle> cycles = run("org.tindalos.principletest.cycle.transitive");
        
        Cycle expectedCycle = new Cycle(
                new PackageReference("org.tindalos.principletest.cycle.transitive.a"),
                new PackageReference("org.tindalos.principletest.cycle.transitive.b"),
                new PackageReference("org.tindalos.principletest.cycle.transitive.c")
                );
        assertEquals(expectedCycle, cycles.get(0));
        assertEquals(1, cycles.size());
    }

    @Test
    public void btwParentAndChild() {
        
        
        List<Cycle> cycles = run("org.tindalos.principletest.cycle.btwparentandchild");
        
        Cycle expectedCycle = new Cycle(
                new PackageReference("org.tindalos.principletest.cycle.btwparentandchild"),
                new PackageReference("org.tindalos.principletest.cycle.btwparentandchild.child")
                );
        assertEquals(expectedCycle, cycles.get(0));
        assertEquals(1, cycles.size());
    }
    
    @Test
    public void complex1() {
        
        List<Cycle> cycles = run("org.tindalos.principletest.cycle.complex1");
        
        Cycle expectedCycle = new Cycle(
                new PackageReference("org.tindalos.principletest.cycle.complex1.left"),
                new PackageReference("org.tindalos.principletest.cycle.complex1.right")
                );
        assertEquals(expectedCycle, cycles.get(0));
        assertEquals(1, cycles.size());
    }
    
    @Test
    public void complex2() {
        
        List<Cycle> cycles = run("org.tindalos.principletest.cycle.complex2");
        
        Cycle expectedCycle = new Cycle(
                new PackageReference("org.tindalos.principletest.cycle.complex2.left"),
                new PackageReference("org.tindalos.principletest.cycle.complex2.right.right")
                );
        assertEquals(expectedCycle, cycles.get(0));
        assertEquals(1, cycles.size());
    }
    
    @Ignore
    @Test
    public void toomanycycles() {
        
        CyclesInSubgraph.LIMIT = 1;
        List<Cycle> cycles = run("org.tindalos.principletest.cycle.toomanycycles");
        
        Cycle expectedCycle = new Cycle(
                new PackageReference("org.tindalos.principletest.cycle.complex2.left"),
                new PackageReference("org.tindalos.principletest.cycle.complex2.right.right")
                );
        assertEquals(expectedCycle, cycles.get(0));
        assertEquals(1, cycles.size());
    }
    
    private List<Cycle> run(String basePackage) {
        init(basePackage);
        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(designQualityCheckConfiguration);
        List<CheckResult> resultList = checkResults.resultList();
        assertEquals(1, resultList.size());
        ADPResult adpResult = (ADPResult) resultList.get(0);
        return adpResult.getCycles();
    }

    private static DesignQualityExpectations prepareChecks() {
        Checks checks = new Checks();
        checks.setPackageCoupling(packageCoupling());
        return checks;
    }

    private static PackageCoupling packageCoupling() {
        PackageCoupling packageCoupling = new PackageCoupling();
        packageCoupling.setADP(new ADP(0));
        return packageCoupling;
    }

}
