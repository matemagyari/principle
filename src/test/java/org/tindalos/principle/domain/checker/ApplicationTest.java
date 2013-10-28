package org.tindalos.principle.domain.checker;

import org.junit.Test;
import org.tindalos.principle.app.service.Application;
import org.tindalos.principle.domain.core.DesignQualityCheckParameters;
import org.tindalos.principle.domain.core.checkerparameter.ACD;
import org.tindalos.principle.domain.core.checkerparameter.ADP;
import org.tindalos.principle.domain.core.checkerparameter.Checks;
import org.tindalos.principle.domain.core.checkerparameter.Layering;
import org.tindalos.principle.domain.core.checkerparameter.PackageCoupling;
import org.tindalos.principle.domain.core.checkerparameter.SAP;
import org.tindalos.principle.domain.core.checkerparameter.SDP;
import org.tindalos.principle.domain.resultprocessing.reporter.Printer;
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

public class ApplicationTest {

	@Test
	public void checkItself() {
		String basePackage = "org.tindalos.principle";
		//String basePackage = "org.tindalos.principle.test";

		Application application = PoorMansDIContainer.getApplication(basePackage);

		Checks checks = prepareChecks();

		try {
			application.doIt(new DesignQualityCheckParameters(checks, basePackage), new ConsolePrinter());
		} catch (ThresholdTrespassedException ex) {

		}

	}

	private Checks prepareChecks() {
		Checks checks = new Checks();

		checks.setLayering(layering());
		checks.setPackageCoupling(packageCoupling());

		return checks;
	}

	private Layering layering() {
		Layering layering = new Layering();
		layering.setLayers("infrastructure", "app", "domain");
		return layering;
	}

	private PackageCoupling packageCoupling() {
		PackageCoupling packageCoupling = new PackageCoupling();
		SAP sap = new SAP();
		sap.setMaxDistance(0.3d);
		packageCoupling.setSAP(sap);
		packageCoupling.setADP(new ADP());
		packageCoupling.setSDP(new SDP());
		packageCoupling.setACD(new ACD());
		return packageCoupling;
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
