package org.tindalos.principle.infrastructure.di;

import java.util.Map;

import org.tindalos.principle.app.service.DesignQualityCheckResultsReporter;
import org.tindalos.principle.app.service.DesignQualityCheckService;
import org.tindalos.principle.app.service.impl.Printer;
import org.tindalos.principle.domain.checker.DesignQualityChecker;
import org.tindalos.principle.domain.checker.PackageAnalyzer;
import org.tindalos.principle.domain.core.PackageSorter;
import org.tindalos.principle.domain.detector.adp.APDResult;
import org.tindalos.principle.domain.detector.adp.APDViolationsReporter;
import org.tindalos.principle.domain.detector.adp.CycleDetector;
import org.tindalos.principle.domain.detector.adp.PackageStructureBuilder;
import org.tindalos.principle.domain.detector.core.CheckResult;
import org.tindalos.principle.domain.detector.core.ViolationsReporter;
import org.tindalos.principle.domain.detector.layering.LayerViolationDetector;
import org.tindalos.principle.domain.detector.layering.LayerViolationsReporter;
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult;
import org.tindalos.principle.domain.detector.sap.SAPResult;
import org.tindalos.principle.domain.detector.sap.SAPViolationDetector;
import org.tindalos.principle.domain.detector.sap.SAPViolationsReporter;
import org.tindalos.principle.domain.detector.sdp.SDPResult;
import org.tindalos.principle.domain.detector.sdp.SDPViolationDetector;
import org.tindalos.principle.domain.detector.sdp.SDPViolationsReporter;
import org.tindalos.principle.infrastructure.service.jdepend.JDependPackageAnalyzer;
import org.tindalos.principle.infrastructure.service.jdepend.JDependRunner;
import org.tindalos.principle.infrastructure.service.jdepend.MetricsCalculator;
import org.tindalos.principle.infrastructure.service.jdepend.PackageFactory;
import org.tindalos.principle.infrastructure.service.jdepend.PackageListFactory;

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
        LayerViolationDetector layerViolationDetector = new LayerViolationDetector();

        DesignQualityChecker designQualityChecker = new DesignQualityChecker(layerViolationDetector, cycleDetector, sdpViolationDetector, sapViolationDetector);
        return new DesignQualityCheckService(packageAnalyzer, designQualityChecker);
    }

	public static DesignQualityCheckResultsReporter getDesignCheckResultsReporter(Printer printer) {
		
		Map<Class<? extends CheckResult>, ViolationsReporter<? extends CheckResult>> reporters = Maps.newHashMap();
		
		reporters.put(APDResult.class, new APDViolationsReporter());
		reporters.put(LayerViolationsResult.class, new LayerViolationsReporter());
		reporters.put(SDPResult.class, new SDPViolationsReporter());
		reporters.put(SAPResult.class, new SAPViolationsReporter());
		
		return new DesignQualityCheckResultsReporter(printer, reporters);
	}
}
