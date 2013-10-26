package org.tindalos.principle.domain.checker;

import org.junit.Test;
import org.tindalos.principle.app.service.DesignQualityCheckResultsReporter;
import org.tindalos.principle.app.service.DesignQualityCheckService;
import org.tindalos.principle.app.service.impl.Printer;
import org.tindalos.principle.domain.core.DesignQualityCheckParameters;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

public class DesignCheckerTest {

	@Test
	public void checkItself() {
		String basePackage = "org.tindalos.principle";
		DesignQualityCheckParameters parameters = new DesignQualityCheckParameters(basePackage, "infrastructure", "app", "domain");
		parameters.setMaxSAPDistance(0.3d);

		DesignQualityCheckService designQualityCheckService = PoorMansDIContainer.getDesignCheckService(basePackage);

		DesignQualityCheckResults results = designQualityCheckService.analyze(parameters);

		DesignQualityCheckResultsReporter reporter = PoorMansDIContainer.getDesignCheckResultsReporter(new ConsolePrinter());
		
		reporter.report(results);

	}

	private static class ConsolePrinter implements Printer {

		public void printWarning(String text) {
			System.err.println(text);
		}

		public void printInfo(String text) {
			System.out.println(text);
		}
	}

}
