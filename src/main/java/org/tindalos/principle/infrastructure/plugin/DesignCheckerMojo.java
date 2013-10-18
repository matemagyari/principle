package org.tindalos.principle.infrastructure.plugin;

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.tindalos.principle.app.service.DesignCheckService;
import org.tindalos.principle.domain.checker.DesignCheckResults;
import org.tindalos.principle.domain.core.DesignCheckerParameters;
import org.tindalos.principle.domain.detector.cycledetector.APDResult;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationsResult;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

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
        
        DesignCheckService designCheckService = PoorMansDIContainer.getDesignCheckService();

        DesignCheckerParameters parameters = buildParameters();
        DesignCheckResults checkResults = designCheckService.analyze(parameters);
        
        if (checkResults.hasErrors()) {
            getLog().warn(checkResults.getErrorReport());

            checkThresholds(checkResults);

        } else {
            getLog().info("All Design tests have passed!");
        }

    }


    private void checkThresholds(DesignCheckResults checkResults) throws MojoFailureException {
        
        APDResult apdResult = checkResults.getResult(APDResult.class);
        LayerViolationsResult layerViolationsResult = checkResults.getResult(LayerViolationsResult.class);

        if (thresholdADPViolations < apdResult.numberOfViolations()
                || thresholdLayerViolations < layerViolationsResult.numberOfViolations()) {

            throw new MojoFailureException("\nProblems!");
        }
    }

    private DesignCheckerParameters buildParameters() {
        DesignCheckerParameters parameters = new DesignCheckerParameters(basePackage);
        parameters.setLayers(layers);
        return parameters;
    }

}
