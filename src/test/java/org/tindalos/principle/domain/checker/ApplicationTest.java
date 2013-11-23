package org.tindalos.principle.domain.checker;

import org.junit.Assert;
import org.junit.Test;
import org.tindalos.principle.app.service.Application;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.expectations.ADP;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.Layering;
import org.tindalos.principle.domain.expectations.PackageCoupling;
import org.tindalos.principle.domain.expectations.SAP;
import org.tindalos.principle.domain.expectations.SDP;
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint;
import org.tindalos.principle.domain.expectations.SubmodulesDefinitionLocation;
import org.tindalos.principle.domain.expectations.cumulativedependency.ACD;
import org.tindalos.principle.domain.resultprocessing.reporter.Printer;
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.plugin.Checks;

public class ApplicationTest {

	@Test
	public void checkItself() {
		String basePackage = "org.tindalos.principle";
		//basePackage = "org.tindalos.principletest.adp";

		Application application = PoorMansDIContainer.getApplication(basePackage);

		DesignQualityExpectations checks = prepareChecks();

		try {
			application.doIt(new DesignQualityCheckConfiguration(checks, basePackage), new ConsolePrinter());
		} catch (ThresholdTrespassedException ex) {

		} catch (Exception ex) {
			Assert.fail(ex.getMessage());
		}

	}

	private DesignQualityExpectations prepareChecks() {
		Checks checks = new Checks();

		checks.setLayering(layering());
		checks.setPackageCoupling(packageCoupling());
		checks.setSubmodulesBlueprint(submodulesBlueprint());

		return checks;
	}

	private SubmodulesBlueprint submodulesBlueprint() {
		return new SubmodulesBlueprint(submodulesDefinitionLocation(),0);
	}

	private SubmodulesDefinitionLocation submodulesDefinitionLocation() {
		return new SubmodulesDefinitionLocation("src/main/resources/principle_blueprint.json");
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
		packageCoupling.setADP(new ADP(0));
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
