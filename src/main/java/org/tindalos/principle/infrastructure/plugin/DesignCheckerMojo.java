package org.tindalos.principle.infrastructure.plugin;

import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.tindalos.principle.app.service.DesignCheckResultsReporter;
import org.tindalos.principle.app.service.DesignCheckService;
import org.tindalos.principle.app.service.impl.Printer;
import org.tindalos.principle.domain.checker.DesignCheckResults;
import org.tindalos.principle.domain.core.DesignCheckerParameters;
import org.tindalos.principle.domain.detector.cycledetector.APDResult;
import org.tindalos.principle.domain.detector.layerviolationdetector.LayerViolationsResult;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

import com.google.common.base.Optional;

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

		Printer printer = new LogPrinter(getLog());
		DesignCheckResultsReporter designCheckResultsReporter = PoorMansDIContainer.getDesignCheckResultsReporter(printer);

		designCheckResultsReporter.report(checkResults);

		checkThresholds(checkResults);

	}

	private void checkThresholds(DesignCheckResults checkResults) throws MojoFailureException {

		Optional<APDResult> apdResult = checkResults.getResult(APDResult.class);
		Optional<LayerViolationsResult> layerViolationsResult = checkResults.getResult(LayerViolationsResult.class);

		if (thresholdADPViolations < apdResult.get().numberOfViolations() 
				|| thresholdLayerViolations < layerViolationsResult.get().numberOfViolations()) {

			throw new MojoFailureException("\nNumber of violations exceeds allowed limits!");
		}
	}

	private DesignCheckerParameters buildParameters() {
		DesignCheckerParameters parameters = new DesignCheckerParameters(basePackage);
		parameters.setLayers(layers);
		return parameters;
	}

}
