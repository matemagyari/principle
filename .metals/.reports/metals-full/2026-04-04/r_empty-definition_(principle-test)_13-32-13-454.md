error id: file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ApplicationModuleTest.scala:buildAnalyzer
file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ApplicationModuleTest.scala
empty definition using pc, found symbol in pc: 
semanticdb not found

found definition using fallback; symbol buildAnalyzer
offset: 693
uri: file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ApplicationModuleTest.scala
text:
```scala
package org.tindalos.guardrails.domain.checker

import org.junit.{Assert, Test}
import org.tindalos.guardrails.domain.plan.AnalysisPlan
import org.tindalos.guardrails.domain.constraints._
import org.tindalos.guardrails.infrastructure.ConsolePrinter
import org.tindalos.guardrails.infrastructure.di.PoorMansDIContainer
import org.tindalos.guardrails.infrastructure.reporters.ReportsDirectoryManager

class ApplicationModuleTest {

  @Test
  def checkItself(): Unit = {

    ReportsDirectoryManager.ensureReportsDirectoryExists()
    val basePackage = "org.tindalos.guardrailss"
    //basePackage = "org.tindalos.guardrailstest"

    TestFixture.setLogger()

    val application = PoorMansDIContainer.bui@@ldAnalyzer(basePackage, new ConsolePrinter())

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
      application.analyze(new AnalysisPlan(constraints, basePackage))
    } catch {
      case ex: Exception =>
        ex.printStackTrace()
        Assert.fail(ex.getMessage())
    }

  }


  private def layering() = new Layering(java.util.List.of("infrastructure", "app", "domain"), 0)


}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 