package org.tindalos.principle.infrastructure.di;

import java.util.Map;

import org.tindalos.principle.app.service.DesignCheckResultsReporter;
import org.tindalos.principle.app.service.DesignCheckService;
import org.tindalos.principle.app.service.impl.Printer;
import org.tindalos.principle.domain.checker.DesignChecker;
import org.tindalos.principle.domain.checker.PackageAnalyzer;
import org.tindalos.principle.domain.detector.core.CheckResult;
import org.tindalos.principle.domain.detector.cycledetector.APDResult;
import org.tindalos.principle.domain.detector.cycledetector.APDViolationsReporter;
import org.tindalos.principle.domain.detector.cycledetector.CycleDetector;
import org.tindalos.principle.domain.detector.cycledetector.PackageStructureBuilder;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationDetector;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationsReporter;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationsResult;
import org.tindalos.principle.domain.reporting.ViolationsReporter;
import org.tindalos.principle.infrastructure.service.jdepend.JDependPackageAnalyzer;
import org.tindalos.principle.infrastructure.service.jdepend.JDependRunner;
import org.tindalos.principle.infrastructure.service.jdepend.PackageBuilder;

import com.google.common.collect.Maps;

public class PoorMansDIContainer {
    
    
    public static DesignCheckService getDesignCheckService() {
        JDependRunner jDependRunner = new JDependRunner();
        PackageBuilder packageBuilder = new PackageBuilder();
        PackageAnalyzer packageAnalyzer = new JDependPackageAnalyzer(jDependRunner, packageBuilder);
        
        LayerViolationDetector layerViolationDetector = new LayerViolationDetector();
        PackageStructureBuilder packageStructureBuilder = new PackageStructureBuilder();
        CycleDetector cycleDetector = new CycleDetector(packageStructureBuilder);
        DesignChecker designChecker = new DesignChecker(layerViolationDetector, cycleDetector);
        return new DesignCheckService(packageAnalyzer, designChecker);
    }

	public static DesignCheckResultsReporter getDesignCheckResultsReporter(Printer printer) {
		
		Map<Class<? extends CheckResult>, ViolationsReporter<? extends CheckResult>> reporters = Maps.newHashMap();
		
		reporters.put(APDResult.class, new APDViolationsReporter());
		reporters.put(LayerViolationsResult.class, new LayerViolationsReporter());
		
		DesignCheckResultsReporter resultsReporter = new DesignCheckResultsReporter(printer);
		resultsReporter.setReporters(reporters);
		return resultsReporter;
	}
}
