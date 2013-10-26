package org.tindalos.principle.infrastructure.plugin;

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.tindalos.principle.app.service.DesignCheckResultsReporter;
import org.tindalos.principle.app.service.DesignCheckService;
import org.tindalos.principle.app.service.impl.Printer;
import org.tindalos.principle.domain.checker.DesignQualityCheckResults;
import org.tindalos.principle.domain.core.DesignQualityCheckParameters;
import org.tindalos.principle.domain.detector.adp.APDResult;
import org.tindalos.principle.domain.detector.layering.LayerViolationsResult;
import org.tindalos.principle.domain.detector.sap.SAPResult;
import org.tindalos.principle.domain.detector.sdp.SDPResult;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.service.jdepend.ClassesToAnalyzeNotFoundException;

import com.google.common.base.Optional;

@Mojo(name = "check")
public class DesignCheckerMojo extends AbstractMojo {

    @Parameter(property = "check.basePackage", defaultValue = "")
    private String basePackage;

    @Parameter(property = "check.thresholdLayerViolations", defaultValue = "0")
    private int thresholdLayerViolations;

    @Parameter(property = "check.thresholdADPViolations", defaultValue = "0")
    private int thresholdADPViolations;

    @Parameter(property = "check.thresholdSAPViolations", defaultValue = "0")
    private int thresholdSAPViolations;

    @Parameter(property = "check.thresholdSAPViolations", defaultValue = "1")
    private Float maxDistance;
    
    @Parameter(property = "check.thresholdSDPViolations", defaultValue = "0")
    private int thresholdSDPViolations;
    
    @Parameter(property = "check.layers")
    private List<String> layers;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            doTheJob();
        } catch (ClassesToAnalyzeNotFoundException ex) {
            getLog().warn(ex.getMessage());
        }

    }

    private void doTheJob() throws MojoFailureException {
    	
    	DesignQualityCheckParameters parameters = buildParameters();
    	
        DesignCheckService designCheckService = PoorMansDIContainer.getDesignCheckService(parameters.getBasePackage());
        

        DesignQualityCheckResults checkResults = designCheckService.analyze(parameters);

        Printer printer = new LogPrinter(getLog());
        DesignCheckResultsReporter designCheckResultsReporter = PoorMansDIContainer
                .getDesignCheckResultsReporter(printer);

        designCheckResultsReporter.report(checkResults);

        checkThresholds(checkResults);
    }

    private void checkThresholds(DesignQualityCheckResults checkResults) throws MojoFailureException {

        Optional<APDResult> apdResult = checkResults.getResult(APDResult.class);
        Optional<LayerViolationsResult> layerViolationsResult = checkResults.getResult(LayerViolationsResult.class);
        Optional<SDPResult> sdpResult = checkResults.getResult(SDPResult.class);
        Optional<SAPResult> sapResult = checkResults.getResult(SAPResult.class);

        boolean breakNecessary = 
        			thresholdADPViolations < apdResult.get().numberOfViolations()
        		 || thresholdLayerViolations < layerViolationsResult.get().numberOfViolations()
        		 || thresholdSDPViolations < sdpResult.get().numberOfViolations()
        		 || thresholdSAPViolations < sapResult.get().numberOfViolations();
        		
        if (breakNecessary) {
            throw new MojoFailureException("\nNumber of violations exceeds allowed limits!");
        }
    }

    private DesignQualityCheckParameters buildParameters() {
        DesignQualityCheckParameters parameters = new DesignQualityCheckParameters(basePackage, layers);
        parameters.setMaxSAPDistance(maxDistance);
        return parameters;
    }

}
