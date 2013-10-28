package org.tindalos.principle.infrastructure.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.tindalos.principle.app.service.Application;
import org.tindalos.principle.domain.core.DesignQualityCheckParameters;
import org.tindalos.principle.domain.core.checkerparameter.Checks;
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.service.jdepend.ClassesToAnalyzeNotFoundException;

@Mojo(name = "check")
public class DesignQualityCheckerMojo extends AbstractMojo {

    @Parameter(property = "check.basePackage", defaultValue = "")
    private String basePackage;
    
    @Parameter(property = "check.checks")
    private Checks checks;

    public void execute() throws MojoExecutionException, MojoFailureException {
    	
    	Application application = PoorMansDIContainer.getApplication(basePackage);
        try {
            application.doIt(new DesignQualityCheckParameters(checks, basePackage), new LogPrinter(getLog()));
        } catch (ClassesToAnalyzeNotFoundException ex) {
            getLog().warn(ex.getMessage());
        } catch (ThresholdTrespassedException ex) {
        	throw new MojoFailureException("\nNumber of violations exceeds allowed limits!");
        }
    }

}
