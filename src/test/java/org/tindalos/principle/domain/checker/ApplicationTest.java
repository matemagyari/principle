package org.tindalos.principle.domain.checker;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.tindalos.principle.app.service.Application;
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration;
import org.tindalos.principle.domain.util.ListConverter;
import org.tindalos.principle.domain.core.logging.ScalaLogger;
import org.tindalos.principle.domain.core.logging.TheLogger;
import org.tindalos.principle.domain.expectations.ADP;
import org.tindalos.principle.domain.expectations.DesignQualityExpectations;
import org.tindalos.principle.domain.expectations.Layering;
import org.tindalos.principle.domain.expectations.PackageCoupling;
import org.tindalos.principle.domain.expectations.SAP;
import org.tindalos.principle.domain.expectations.SDP;
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint;
import org.tindalos.principle.domain.expectations.cumulativedependency.ACD;
import org.tindalos.principle.domain.resultprocessing.reporter.Printer;
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;
import org.tindalos.principle.infrastructure.plugin.Checks;

public class ApplicationTest {

	@Test
	public void checkItself() {
		String basePackage = "org.tindalos.principle";
		//basePackage = "org.tindalos.principletest";
		
		TheLogger.setLogger(new ScalaLogger() {
            public void info(String msg) {
                System.out.println(msg);
            }
            public void error(String msg) {
                System.err.println(msg);
            }
        });

		Application application = PoorMansDIContainer.getApplication(basePackage);

		DesignQualityExpectations checks = prepareChecks();

		try {
			application.doIt(new DesignQualityCheckConfiguration(checks, basePackage), new ConsolePrinter());
		} catch (ThresholdTrespassedException ex) {

		} catch (Exception ex) {
			ex.printStackTrace();
			Assert.fail(ex.getMessage());
		}

	}

	private DesignQualityExpectations prepareChecks() {
		Checks checks = new Checks();

		checks.layering_$eq(layering());
		checks.packageCoupling_$eq(packageCoupling());
		//checks.setSubmodulesBlueprint(submodulesBlueprint());

		return checks;
	}

	private SubmodulesBlueprint submodulesBlueprint() {
		return new SubmodulesBlueprint(submodulesDefinitionLocation(),0);
	}

	private String submodulesDefinitionLocation() {
		return "src/main/resources/principle_blueprint.yaml";
	}

	private Layering layering() {
		List<String> strs = Arrays.asList("infrastructure", "app", "domain");
		Layering layering = new Layering(ListConverter.convert(strs),0);
		return layering;
	}

	private PackageCoupling packageCoupling() {
		PackageCoupling packageCoupling = new PackageCoupling();
		SAP sap = new SAP(0,0.3d);
		packageCoupling.sap_$eq(sap);
		packageCoupling.adp_$eq(new ADP());
		packageCoupling.sdp_$eq(new SDP());
		packageCoupling.acd_$eq(new ACD());
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
