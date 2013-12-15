package org.tindalos.principle.domain.checker;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.tindalos.principle.domain.core.Cycle;
import org.tindalos.principle.domain.core.CyclesInSubgraph;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.core.Package;
import org.tindalos.principle.domain.core.PackageReference;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.detector.adp.ADPResult;
import org.tindalos.principle.domain.detector.submodulesblueprint.Submodule;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleId;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintCheckResult;
import org.tindalos.principle.domain.expectations.ADP;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.PackageCoupling;
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint;
import org.tindalos.principle.domain.expectations.SubmodulesDefinitionLocation;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.plugin.Checks;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class BlueprintTest {

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
        
        SubmodulesBlueprintCheckResult result = run("org.tindalos.principletest.submodulesblueprint");
        
        Map<Submodule, Set<Submodule>> illegalDependencies = result.illegalDependencies();
        Map<Submodule, Set<Submodule>> missingDependencies = result.missingDependencies();
        
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
    
    private void assertDependencies(Map<Submodule, Set<Submodule>> actualDependencies, Submodule referer, Submodule... referees) {
		Map<Submodule, Set<Submodule>> expected = Maps.newHashMap();
		assertEquals(actualDependencies, expected);
	}

	private static Submodule fakeSubmodule(String name) {
    	Set<Package> empty = Sets.newHashSet();
		return new Submodule(new SubmoduleId(name), empty);
    }
    
       
    private SubmodulesBlueprintCheckResult run(String basePackage) {
        init(basePackage);
        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(designQualityCheckConfiguration);
        List<CheckResult> resultList = checkResults.resultList();
        assertEquals(1, resultList.size());
        return (SubmodulesBlueprintCheckResult) resultList.get(0);
    }

    private static DesignQualityExpectations prepareChecks() {
        Checks checks = new Checks();
        checks.setSubmodulesBlueprint(submodulesBlueprint());
        return checks;
    }

	private static SubmodulesBlueprint submodulesBlueprint() {
		SubmodulesDefinitionLocation submodulesDefinitionLocation = new SubmodulesDefinitionLocation("src/test/resources/principle_blueprint_test.yaml");
		return new SubmodulesBlueprint(submodulesDefinitionLocation, 0);
	}

}
