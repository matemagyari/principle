package org.principle.infrastructure.plugin;

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.principle.app.service.DesignCheckService;
import org.principle.app.service.PackageAnalyzer;
import org.principle.domain.checker.DesignCheckResults;
import org.principle.domain.checker.DesignChecker;
import org.principle.domain.core.DesingCheckerParameters;
import org.principle.domain.detector.cycledetector.CycleDetector;
import org.principle.domain.detector.layerviolationdetector.LayerViolationDetector;
import org.principle.infrastructure.service.jdepend.JDependPackageAnalyzer;
import org.principle.infrastructure.service.jdepend.JDependRunner;
import org.principle.infrastructure.service.jdepend.PackageBuilder;

@Mojo(name = "designcheck")
public class DesignCheckerMojo extends AbstractMojo {

    @Parameter(property = "designcheck.basePackage", defaultValue = "")
    private String basePackage;

    @Parameter(property = "designcheck.thresholdLayerViolations", defaultValue = "0")
    private int thresholdLayerViolations;

    @Parameter(property = "designcheck.thresholdADPViolations", defaultValue = "0")
    private int thresholdADPViolations;

    @Parameter(property = "designcheck.layers")
    private List<String> layers;

    public void execute() throws MojoExecutionException, MojoFailureException {

        DesingCheckerParameters parameters = buildParameters();
        DesignCheckService designCheckService = getDesingCheckService(parameters);
        
        DesignCheckResults checkResults = designCheckService.analyze(parameters);

        if (checkResults.hasErrors()) {
            getLog().warn(checkResults.getErrorReport());

            checkThresholds(checkResults);

        } else {
            getLog().info("All Design tests have passed!");
        }

    }

    private DesignCheckService getDesingCheckService(DesingCheckerParameters parameters) {
        JDependRunner jDependRunner = new JDependRunner();
        PackageBuilder packageBuilder = new PackageBuilder();
        PackageAnalyzer packageAnalyzer = new JDependPackageAnalyzer(jDependRunner, packageBuilder);
        
        LayerViolationDetector layerViolationDetector = new LayerViolationDetector(parameters);
        CycleDetector cycleDetector = new CycleDetector(parameters.getBasePackage());
        DesignChecker designChecker = new DesignChecker(layerViolationDetector, cycleDetector);
        DesignCheckService designCheckService = new DesignCheckService(packageAnalyzer, designChecker);
        return designCheckService;
    }

    private void checkThresholds(DesignCheckResults checkResults) throws MojoFailureException {

        if (thresholdADPViolations < checkResults.numOfADPViolations()
                || thresholdLayerViolations < checkResults.numOfLayerViolations()) {

            throw new MojoFailureException("\nProblems!");
        }
    }

    private DesingCheckerParameters buildParameters() {
        DesingCheckerParameters parameters = new DesingCheckerParameters(basePackage);
        parameters.setLayers(layers);
        return parameters;
    }

}
