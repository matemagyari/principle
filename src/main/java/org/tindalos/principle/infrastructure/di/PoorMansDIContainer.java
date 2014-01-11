package org.tindalos.principle.infrastructure.di;

import java.util.List;
import java.util.Map;

import org.tindalos.principle.app.service.Application;
import org.tindalos.principle.domain.checker.DesignQualityCheckService;
import org.tindalos.principle.domain.checker.DesignQualityDetectorsRunner;
import org.tindalos.principle.domain.checker.PackageAnalyzer;
import org.tindalos.principle.domain.core.PackageSorter;
import org.tindalos.principle.domain.coredetector.CheckResult;
import org.tindalos.principle.domain.coredetector.Detector;
import org.tindalos.principle.domain.coredetector.ViolationsReporter;
import org.tindalos.principle.domain.detector.acd.ACDDetector;
import org.tindalos.principle.domain.detector.acd.ACDResult;
import org.tindalos.principle.domain.detector.adp.ADPResult;
import org.tindalos.principle.domain.detector.adp.CycleDetector;
import org.tindalos.principle.domain.detector.adp.PackageStructureBuilder;
import org.tindalos.principle.domain.detector.layering.LayerViolationDetector;
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult;
import org.tindalos.principle.domain.detector.sap.SAPResult;
import org.tindalos.principle.domain.detector.sap.SAPViolationDetector;
import org.tindalos.principle.domain.detector.sdp.SDPResult;
import org.tindalos.principle.domain.detector.sdp.SDPViolationDetector;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleDefinitionsProvider;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmoduleFactory;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintCheckResult;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesBlueprintViolationDetector;
import org.tindalos.principle.domain.detector.submodulesblueprint.SubmodulesFactory;
import org.tindalos.principle.domain.resultprocessing.reporter.DesignQualityCheckResultsReporter;
import org.tindalos.principle.infrastructure.detector.submodulesblueprint.YAMLBasedSubmodulesBlueprintProvider;
import org.tindalos.principle.infrastructure.reporters.ACDViolationsReporter;
import org.tindalos.principle.infrastructure.reporters.ADPViolationsReporter;
import org.tindalos.principle.infrastructure.reporters.LayerViolationsReporter;
import org.tindalos.principle.infrastructure.reporters.SAPViolationsReporter;
import org.tindalos.principle.infrastructure.reporters.SDPViolationsReporter;
import org.tindalos.principle.infrastructure.reporters.SubmodulesBlueprintViolationsReporter;
import org.tindalos.principle.infrastructure.service.jdepend.JDependPackageAnalyzer;
import org.tindalos.principle.infrastructure.service.jdepend.JDependRunner;
import org.tindalos.principle.infrastructure.service.jdepend.MetricsCalculator;
import org.tindalos.principle.infrastructure.service.jdepend.PackageFactory;
import org.tindalos.principle.infrastructure.service.jdepend.PackageListFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PoorMansDIContainer {
    
    
	public static DesignQualityCheckService getDesignCheckService(String basePackage) {
        JDependRunner jDependRunner = new JDependRunner();
        PackageFactory packageFactory = new PackageFactory(new MetricsCalculator(),basePackage);
        PackageSorter packageSorter = new PackageSorter();
		PackageListFactory packageListFactory = new PackageListFactory(packageFactory, packageSorter);
        PackageAnalyzer packageAnalyzer = new JDependPackageAnalyzer(jDependRunner, packageListFactory);
        
        PackageStructureBuilder packageStructureBuilder = new PackageStructureBuilder(packageSorter);
        
        CycleDetector cycleDetector = new CycleDetector(packageStructureBuilder);
        SDPViolationDetector sdpViolationDetector = new SDPViolationDetector();
        SAPViolationDetector sapViolationDetector = new SAPViolationDetector();
        ACDDetector acdDetector = new ACDDetector(packageStructureBuilder);
        LayerViolationDetector layerViolationDetector = new LayerViolationDetector();
        
       // SubmoduleDefinitionsProvider submoduleDefinitionsProvider = new JSONBasedSubmodulesBlueprintProvider();
        SubmoduleDefinitionsProvider submoduleDefinitionsProvider = new YAMLBasedSubmodulesBlueprintProvider();
		SubmoduleFactory submoduleFactory = new SubmoduleFactory();
		SubmodulesFactory submodulesFactory = new SubmodulesFactory(packageStructureBuilder, submoduleDefinitionsProvider, submoduleFactory);
		SubmodulesBlueprintViolationDetector submodulesBlueprintViolationDetector = new SubmodulesBlueprintViolationDetector(submodulesFactory );

        List<Detector> detectors = Lists.newArrayList(layerViolationDetector, cycleDetector, sdpViolationDetector, sapViolationDetector, acdDetector, submodulesBlueprintViolationDetector);
		DesignQualityDetectorsRunner designQualityDetectorsRunner = new DesignQualityDetectorsRunner(detectors);
        return new DesignQualityCheckService(packageAnalyzer, designQualityDetectorsRunner);
    }

	private static DesignQualityCheckResultsReporter getDesignCheckResultsReporter() {
		
		Map<Class<? extends CheckResult>, ViolationsReporter<? extends CheckResult>> reporters = Maps.newHashMap();
		
		reporters.put(ADPResult.class, new ADPViolationsReporter());
		reporters.put(LayerViolationsResult.class, new LayerViolationsReporter());
		reporters.put(SDPResult.class, new SDPViolationsReporter());
		reporters.put(SAPResult.class, new SAPViolationsReporter());
		reporters.put(ACDResult.class, new ACDViolationsReporter());
		reporters.put(SubmodulesBlueprintCheckResult.class, new SubmodulesBlueprintViolationsReporter());
		
		return new DesignQualityCheckResultsReporter(reporters);
	}
	
	public static Application getApplication(String basePackage) {
		return new Application(getDesignCheckService(basePackage), getDesignCheckResultsReporter() );
	}

}
