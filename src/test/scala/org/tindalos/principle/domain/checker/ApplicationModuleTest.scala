package org.tindalos.principle.domain.checker

import org.junit.{Assert, Test}
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.constraints._
import org.tindalos.principle.infrastructure.ConsolePrinter
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

    val constraints = Constraints.builder()
      .layering(layering())
      .packageCoupling(PackageCouplingConstraints.builder()
        .sap(new SAP(0, 0.3d))
        .adp(new ADP())
        .sdp(new SDP())
        .acd(new ACD())
        .grouping(Grouping.of())
        .build())
      .build()

    try {
      runAnalysis(new AnalysisPlan(constraints, basePackage))
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        Assert.fail(ex.getMessage())
    }

  }


  private def layering() = new Layering(java.util.List.of("infrastructure", "app", "domain"), 0)


}