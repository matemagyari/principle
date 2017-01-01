package org.tindalos.principle.domain.checker

import org.junit.{Assert, Test}
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.domain.resultprocessing.reporter.Printer
import org.tindalos.principle.domain.resultprocessing.thresholdchecker.ThresholdTrespassedException
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager

class ApplicationModuleTest {

  @Test
  def checkItself(): Unit = {

    ReportsDirectoryManager.ensureReportsDirectoryExists()
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

  private def prepareChecks() =
    Checks(layering = layering(), packageCoupling = packageCoupling())

  private val submodulesDefinitionLocation = "src/main/resources/principle_blueprint.yaml"
  private val submodulesBlueprint = new SubmodulesBlueprint(submodulesDefinitionLocation, 0)

  private def layering() = new Layering(List("infrastructure", "app", "domain"), 0)

  private def packageCoupling() =
    PackageCoupling(
      sap = SAP(0, 0.3d),
      adp = ADP(),
      sdp = SDP(),
      acd = ACD(),
      grouping = new Grouping())

  private class ConsolePrinter extends Printer {

    def printWarning(text: String) = {
      System.err.println(text)
    }

    def printInfo(text: String) = {
      System.out.println(text)
    }
  }

}