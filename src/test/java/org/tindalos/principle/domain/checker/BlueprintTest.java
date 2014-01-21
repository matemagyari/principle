package org.tindalos.principle.domain.checker;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.detector.submodulesblueprint.Submodule;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleId;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintCheckResult;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.util.ListConverter;
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.plugin.Checks;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class BlueprintTest {

    @BeforeClass
    public static void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void missingAndIllegal() {
        
        SubmodulesBlueprintCheckResult result = run("org.tindalos.principletest.submodulesblueprint","src/test/resources/principle_blueprint_test.yaml");
        
        Map<Submodule, Set<Submodule>> illegalDependencies = ListConverter.convertMapSet(result.illegalDependencies());
        Map<Submodule, Set<Submodule>> missingDependencies = ListConverter.convertMapSet(result.missingDependencies());
        
        Submodule mod1 = fakeSubmodule("MOD1");
        Submodule mod2 = fakeSubmodule("MOD2");
        Submodule mod3 = fakeSubmodule("MOD3");
        
        Map<Submodule, Set<Submodule>> expectedIllegalDependencies = Maps.newHashMap();
        expectedIllegalDependencies.put(mod3, Sets.newHashSet(mod2));

        Map<Submodule, Set<Submodule>> expectedMissingDependencies = Maps.newHashMap();
        expectedMissingDependencies.put(mod1, Sets.newHashSet(mod2));
        
        assertEquals(expectedIllegalDependencies, illegalDependencies);
        assertEquals(expectedMissingDependencies, missingDependencies);
    }
    
    @Test
    public void overlapping() {
    	SubmodulesBlueprintCheckResult result = run("org.tindalos.principletest.submodulesblueprint","src/test/resources/principle_blueprint_test_overlapping.yaml");
    	System.out.println(result);
    }
    
    private void assertDependencies(Map<Submodule, Set<Submodule>> actualDependencies, Submodule referer, Submodule... referees) {
		Map<Submodule, Set<Submodule>> expected = Maps.newHashMap();
		assertEquals(actualDependencies, expected);
	}

	private static Submodule fakeSubmodule(String name) {
    	Set<Package> empty = Sets.newHashSet();
		return new Submodule(new SubmoduleId(name), ListConverter.convert(empty), ListConverter.convert((new HashSet<SubmoduleId>())));
    }
    
       
    private SubmodulesBlueprintCheckResult run(String basePackage, String location) {
        DesignQualityExpectations checks = prepareChecks(location);
        DesignQualityCheckService designQualityCheckService = PoorMansDIContainer.getDesignCheckService(basePackage);
        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(new DesignQualityCheckConfiguration(checks, basePackage));
        List<CheckResult> resultList = checkResults.resultList();
        assertEquals(1, resultList.size());
        return (SubmodulesBlueprintCheckResult) resultList.get(0);
    }

    private static DesignQualityExpectations prepareChecks(String location) {
        Checks checks = new Checks();
        checks.submodulesBlueprint_$eq(submodulesBlueprint(location));
        return checks;
    }

	private static SubmodulesBlueprint submodulesBlueprint(String location) {
		return new SubmodulesBlueprint(location, 0);
	}

}
