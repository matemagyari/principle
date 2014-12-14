package org.tindalos.principle.domain.checker

import org.junit.{Assert, Test}
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.expectations.{ADP, Layering, PackageCoupling, SAP, SDP, SubmodulesBlueprint}
import org.tindalos.principle.domain.expectations.cumulativedependency.ACD
import org.tindalos.principle.domain.resultprocessing.reporter.Printer
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.plugin.Checks

class ApplicationTest {

  @Test
  def checkItself(): Unit = {
    val basePackage = "org.tindalos.principle"
    //basePackage = "org.tindalos.principletest"

    TestFixture.setLogger()

    val runAnalysis = PoorMansDIContainer.buildAnalyzer(basePackage, new ConsolePrinter())

    val checks = prepareChecks()

    try {
      runAnalysis(new AnalysisPlan(checks, basePackage))
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