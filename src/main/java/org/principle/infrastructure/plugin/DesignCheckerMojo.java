package org.principle.infrastructure.plugin;

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.principle.domain.checker.DesignCheckResults;
import org.principle.domain.checker.DesignChecker;
import org.principle.domain.core.DesingCheckerParameters;

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
    
    
    private DesignChecker designChecker;

    public void execute() throws MojoExecutionException, MojoFailureException {
        
        DesingCheckerParameters parameters = buildParameters();
        designChecker = new DesignChecker(parameters);

        DesignCheckResults checkResults = designChecker.execute();

        if (checkResults.hasErrors()) {
            getLog().warn(checkResults.getErrorReport());

            checkThresholds(checkResults);

        } else {
            getLog().info("All Design tests have passed!");
        }

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
