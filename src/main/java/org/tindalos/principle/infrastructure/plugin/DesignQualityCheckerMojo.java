package org.tindalos.principle.infrastructure.plugin;

import org.apache.commons.lang3.Validate;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.tindalos.principle.app.service.Application;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.core.logging.Logger;
import org.tindalos.principle.domain.core.logging.TheLogger;
import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException;
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
    	
    	Validate.notNull(checks,"Missing <checks> tag!");
    	Validate.notBlank(basePackage,"Missing <basePackage> tag!");
    	
    	TheLogger.setLogger(new Logger() {
            public void info(String msg) {
                getLog().info(msg);
            }
        });
    	
    	Application application = PoorMansDIContainer.getApplication(basePackage);
        try {
            application.doIt(new DesignQualityCheckConfiguration(checks, basePackage), new LogPrinter(getLog()));
        } catch (ClassesToAnalyzeNotFoundException ex) {
            getLog().warn(ex.getMessage());
        } catch (ThresholdTrespassedException ex) {
        	throw new MojoFailureException("\nNumber of violations exceeds allowed limits!");
        } catch (InvalidConfigurationException ex) {
        	throw new MojoFailureException(ex.getMessage());
        }
    }

}
