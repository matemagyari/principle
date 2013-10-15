package org.principle.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

@Mojo(name = "designcheck")
public class DesignCheckerMojo extends AbstractMojo {

    @Parameter(property = "designcheck.basePackage", defaultValue = "")
    private String basePackage;
    @Parameter(property = "designcheck.appPackage", defaultValue = "")
    private String appPackage;
    @Parameter(property = "designcheck.infrastructurePackage", defaultValue = "")
    private String infrastructurePackage;
    @Parameter(property = "designcheck.domainPackage", defaultValue = "")
    private String domainPackage;
    @Parameter(property = "designcheck.breakIfCycleDetected", defaultValue = "false")
    private boolean breakIfCycleDetected;
    @Parameter(property = "designcheck.breakIfDDDLayerViolationDetected", defaultValue = "0")
    private int thresholdDDDLayerViolations;

    private DesignChecker designChecker;

    public void execute() throws MojoExecutionException, MojoFailureException {

        DesingCheckerParameters parameters = defaultParameters();
        designChecker = new DesignChecker(parameters);

        DesignCheckResults checkResults = designChecker.execute(parameters);

        if (checkResults.hasErrors()) {
            getLog().warn(checkResults.getErrorReport());

            checkThresholds(checkResults);

        } else {
            getLog().info("All Design tests have passed!");
        }

    }

    private void checkThresholds(DesignCheckResults checkResults) throws MojoFailureException {
        
        if (breakIfCycleDetected && checkResults.isCyclesDetected()
                || thresholdDDDLayerViolations < checkResults.numOfViolations()) {
            
            throw new MojoFailureException("\nCycle detected: " + checkResults.isCyclesDetected()
                    + ".\nDDD layer violations (" + thresholdDDDLayerViolations + " permissible): "
                    + checkResults.numOfViolations());
        }
    }

    private DesingCheckerParameters defaultParameters() {
        checkNotEmpy("basePackage", basePackage);
        checkNotEmpy("appPackage", appPackage);
        checkNotEmpy("domainPackage", domainPackage);
        checkNotEmpy("infrastructurePackage", infrastructurePackage);
        return new DesingCheckerParameters(basePackage, appPackage, domainPackage, infrastructurePackage);
    }

    private void checkNotEmpy(String name, String value) {
        if (StringUtils.isBlank(value)) {
            throw new RuntimeException(name + " is empty");
        }
    }

}
