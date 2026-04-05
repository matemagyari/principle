error id: file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ApplicationModuleTest.scala:buildAnalyzer
file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ApplicationModuleTest.scala
empty definition using pc, found symbol in pc: 
semanticdb not found

found definition using fallback; symbol buildAnalyzer
offset: 693
uri: file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ApplicationModuleTest.scala
text:
```scala
package org.tindalos.principle.domain.checker

import org.junit.{Assert, Test}
import org.tindalos.principle.domain.plan.AnalysisPlan
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