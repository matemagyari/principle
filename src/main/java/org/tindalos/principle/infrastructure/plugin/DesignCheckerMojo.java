package org.tindalos.principle.infrastructure.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.tindalos.principle.app.service.DesignQualityCheckResultsReporter;
import org.tindalos.principle.app.service.DesignQualityCheckService;
import org.tindalos.principle.app.service.impl.Printer;
import org.tindalos.principle.domain.checker.DesignQualityCheckResults;
import org.tindalos.principle.domain.core.DesignQualityCheckParameters;
import org.tindalos.principle.domain.detector.adp.APDResult;
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult;
import org.tindalos.principle.domain.detector.sap.SAPResult;
import org.tindalos.principle.domain.detector.sdp.SDPResult;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.plugin.parameter.Checks;
import org.tindalos.principle.infrastructure.plugin.parameter.PackageCoupling;
import org.tindalos.principle.infrastructure.service.jdepend.ClassesToAnalyzeNotFoundException;

import com.google.common.base.Optional;

@Mojo(name = "check")
public class DesignCheckerMojo extends AbstractMojo {

    @Parameter(property = "check.basePackage", defaultValue = "")
    private String basePackage;
    
    @Parameter(property = "check.checks")
    private Checks checks;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            doTheJob();
        } catch (ClassesToAnalyzeNotFoundException ex) {
            getLog().warn(ex.getMessage());
        }

    }

    private void doTheJob() throws MojoFailureException {
    	
    	DesignQualityCheckParameters parameters = buildParameters();
    	
        DesignQualityCheckService designQualityCheckService = PoorMansDIContainer.getDesignCheckService(parameters.getBasePackage());
        

        DesignQualityCheckResults checkResults = designQualityCheckService.analyze(parameters);

        Printer printer = new LogPrinter(getLog());
        DesignQualityCheckResultsReporter designQualityCheckResultsReporter = PoorMansDIContainer
                .getDesignCheckResultsReporter(printer);

        designQualityCheckResultsReporter.report(checkResults);

        checkThresholds(checkResults);
    }

    private void checkThresholds(DesignQualityCheckResults checkResults) throws MojoFailureException {

        Optional<APDResult> apdResult = checkResults.getResult(APDResult.class);
        Optional<LayerViolationsResult> layerViolationsResult = checkResults.getResult(LayerViolationsResult.class);
        Optional<SDPResult> sdpResult = checkResults.getResult(SDPResult.class);
        Optional<SAPResult> sapResult = checkResults.getResult(SAPResult.class);

        PackageCoupling packageCoupling = checks.getPackageCoupling();
        boolean breakNecessary = 
        			packageCoupling.getADP().getViolationsThreshold() < apdResult.get().numberOfViolations()
        			|| packageCoupling.getSDP().getViolationsThreshold() < sdpResult.get().numberOfViolations()
        			|| packageCoupling.getSAP().getViolationsThreshold() < sapResult.get().numberOfViolations()
        		 	|| checks.getLayering().getViolationsThreshold() < layerViolationsResult.get().numberOfViolations();
        		
        if (breakNecessary) {
            throw new MojoFailureException("\nNumber of violations exceeds allowed limits!");
        }
    }

    private DesignQualityCheckParameters buildParameters() {
        DesignQualityCheckParameters parameters = new DesignQualityCheckParameters(basePackage, checks.getLayering().getLayers());
        parameters.setMaxSAPDistance(checks.getPackageCoupling().getSAP().getMaxDistance());
        return parameters;
    }

}
