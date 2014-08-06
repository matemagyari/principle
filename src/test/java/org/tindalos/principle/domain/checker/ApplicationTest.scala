package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.expectations.PackageCoupling
import org.tindalos.principle.domain.expectations.cumulativedependency.ACD
import org.tindalos.principle.domain.expectations.Layering
import org.tindalos.principle.domain.expectations.ADP
import org.tindalos.principle.domain.expectations.SDP
import org.tindalos.principle.domain.expectations.SAP
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException
import org.junit.Test
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException
import org.junit.Assert
import org.tindalos.principle.infrastructure.plugin.Checks
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint
import org.tindalos.principle.domain.resultprocessing.reporter.Printer

class ApplicationTest {

  @Test
  def checkItself():Unit = {
    val basePackage = "org.tindalos.principle"
    //basePackage = "org.tindalos.principletest"

    TestFixture.setLogger()

    val application = PoorMansDIContainer.getApplication(basePackage)

    val checks = prepareChecks()

    try {
      application.run(new DesignQualityCheckConfiguration(checks, basePackage), new ConsolePrinter())
    } catch {
      case ex: ThresholdTrespassedException =>
      case ex: Exception =>
        ex.printStackTrace()
        Assert.fail(ex.getMessage())
    }

  }

  private def prepareChecks() = {
    val checks = new Checks()

    checks.layering = layering()
    checks.packageCoupling = packageCoupling()
    //checks.setSubmodulesBlueprint(submodulesBlueprint())
    checks
  }

  private val submodulesDefinitionLocation = "src/main/resources/principle_blueprint.yaml"
  private val submodulesBlueprint = new SubmodulesBlueprint(submodulesDefinitionLocation, 0)

  private def layering() = new Layering(List("infrastructure", "app", "domain"), 0)

  private def packageCoupling() = {
    val packageCoupling = new PackageCoupling()
    packageCoupling.sap = new SAP(0, 0.3d)
    packageCoupling.adp = new ADP()
    packageCoupling.sdp = new SDP()
    packageCoupling.acd = new ACD()
    packageCoupling
  }

  private class ConsolePrinter extends Printer {

    def printWarning(text: String) = {
      System.err.println(text)
    }

    def printInfo(text: String) = {
      System.out.println(text)
    }
  }

}