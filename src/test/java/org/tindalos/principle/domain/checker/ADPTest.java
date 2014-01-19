package org.tindalos.principle.domain.checker;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.core.ListConverter;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.detector.adp.ADPResult;
import org.tindalos.principle.domain.expectations.ADP;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.PackageCoupling;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.plugin.Checks;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ADPTest {

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
        
        Map<PackageReference, Set<Cycle>> result = run("org.tindalos.principletest.cycle.simple");
        
        Cycle expectedCycle = new Cycle(new PackageReference("org.tindalos.principletest.cycle.simple.left"),new PackageReference("org.tindalos.principletest.cycle.simple.right"));
        Map<PackageReference, Set<Cycle>> expected = Maps.newHashMap();
        expected.put(new PackageReference("org.tindalos.principletest.cycle.simple.left"), Sets.newHashSet(expectedCycle));
        
        assertEquals(expected, result);
    }
    
    @Test
    public void transitive() {
        
        
        Map<PackageReference, Set<Cycle>> result = run("org.tindalos.principletest.cycle.transitive");
        
        Cycle expectedCycle = new Cycle(
                new PackageReference("org.tindalos.principletest.cycle.transitive.a"),
                new PackageReference("org.tindalos.principletest.cycle.transitive.b"),
                new PackageReference("org.tindalos.principletest.cycle.transitive.c")
                );
        
        Map<PackageReference, Set<Cycle>> expected = Maps.newHashMap();
        expected.put(new PackageReference("org.tindalos.principletest.cycle.transitive.a"), Sets.newHashSet(expectedCycle));
        
        assertEquals(expected, result);
    }

    @Test
    public void btwParentAndChild() {
        
        
        Map<PackageReference, Set<Cycle>> result = run("org.tindalos.principletest.cycle.btwparentandchild");
        
        Cycle expectedCycle = new Cycle(
                new PackageReference("org.tindalos.principletest.cycle.btwparentandchild"),
                new PackageReference("org.tindalos.principletest.cycle.btwparentandchild.child")
                );

        Map<PackageReference, Set<Cycle>> expected = Maps.newHashMap();
        expected.put(new PackageReference("org.tindalos.principletest.cycle.btwparentandchild"), Sets.newHashSet(expectedCycle));
        
        assertEquals(expected, result);
    }
    
    @Test
    public void complex1() {
        
        Map<PackageReference, Set<Cycle>> result = run("org.tindalos.principletest.cycle.complex1");
        
        Cycle expectedCycle = new Cycle(
                new PackageReference("org.tindalos.principletest.cycle.complex1.left"),
                new PackageReference("org.tindalos.principletest.cycle.complex1.right")
                );
        Map<PackageReference, Set<Cycle>> expected = Maps.newHashMap();
        expected.put(new PackageReference("org.tindalos.principletest.cycle.complex1.right"), Sets.newHashSet(expectedCycle));
        
        assertEquals(expected, result);
    }
    
    @Test
    public void complex2() {
        
        Map<PackageReference, Set<Cycle>> result = run("org.tindalos.principletest.cycle.complex2");
        
        Cycle expectedCycle = new Cycle(
                new PackageReference("org.tindalos.principletest.cycle.complex2.left"),
                new PackageReference("org.tindalos.principletest.cycle.complex2.right.right")
                );
        Map<PackageReference, Set<Cycle>> expected = Maps.newHashMap();
        expected.put(new PackageReference("org.tindalos.principletest.cycle.complex2.right.right"), Sets.newHashSet(expectedCycle));
        
        assertEquals(expected, result);
    }
    
    @Ignore
    @Test
    public void toomanycycles() {
        
        Map<PackageReference, Set<Cycle>> cycles = run("org.tindalos.principletest.cycle.toomanycycles");
        
        Cycle expectedCycle = new Cycle(
                new PackageReference("org.tindalos.principletest.cycle.complex2.left"),
                new PackageReference("org.tindalos.principletest.cycle.complex2.right.right")
                );
        assertEquals(expectedCycle, cycles.get(0));
        assertEquals(1, cycles.size());
    }
    
    private Map<PackageReference, Set<Cycle>> run(String basePackage) {
        init(basePackage);
        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(designQualityCheckConfiguration);
        List<CheckResult> resultList = checkResults.resultList();
        assertEquals(1, resultList.size());
        ADPResult adpResult = (ADPResult) resultList.get(0);
        return ListConverter.convert(adpResult.cyclesByBreakingPoints());
    }

    private static DesignQualityExpectations prepareChecks() {
        Checks checks = new Checks();
        checks.packageCoupling_$eq(packageCoupling());
        return checks;
    }

    private static PackageCoupling packageCoupling() {
        PackageCoupling packageCoupling = new PackageCoupling();
        packageCoupling.adp_$eq(new ADP(0));
        return packageCoupling;
    }

}
