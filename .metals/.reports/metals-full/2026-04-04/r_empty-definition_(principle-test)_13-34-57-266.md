error id: file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ACDTest.scala:PoorMansDIContainer
file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ACDTest.scala
empty definition using pc, found symbol in pc: 
semanticdb not found

found definition using fallback; symbol PoorMansDIContainer
offset: 660
uri: file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ACDTest.scala
text:
```scala
package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.plan.AnalysisInput
import org.tindalos.principle.domain.analyzers.acd._
import org.tindalos.principle.domain.plan.AnalysisPlan
import org.tindalos.principle.domain.constraints._
import org.tindalos.principle.domain.core.packages.PackageWithMetrics
import org.tindalos.principle.infrastructure.service.jdepend.JDependBasedPackageListBuilder
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import scala.collection.JavaConverters._

class ACDTest {

  var plan: AnalysisPlan = null
  val analysisRunner = PoorMansDIContainer@@.buildAnalysisRunner()
  var expectations: Constraints = prepareConstraints()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  def init(basePackage: String) = {
    plan = new AnalysisPlan(expectations, basePackage)
  }

  @Test
  def simple1() = {

    val result = run("org.tindalos.principletest.acd.simple1")

    assertEquals(1, result, 0.01)
  }

  @Test
  def simple11() {

    val result = run("org.tindalos.principletest.acd.simple11")

    assertEquals(1.5, result, 0.01)
  }

  @Test
  def simple() {

    val result = run("org.tindalos.principletest.acd.simple")

    assertEquals(2.5, result, 0.01)
  }

  @Test
  def cyclic3() {

    val result = run("org.tindalos.principletest.acd.cyclic3")

    assertEquals(3, result, 0.01)
  }

  @Test
  def cyclic6() {

    val result = run("org.tindalos.principletest.acd.cycle6")

    assertEquals(4.33, result, 0.01)
  }

  @Test
  def cyclic62() {

    val result = run("org.tindalos.principletest.acd.cycle6_2")

    assertEquals(2, result, 0.01)
  }

  private def run(basePackage: String) = {
    init(basePackage)
    val packageListProducer = new JDependBasedPackageListBuilder(basePackage)
    val packageList = packageListProducer.build()
    val packageInputs = packageList.asScala.map(p => p: PackageWithMetrics).toList
    val result = analysisRunner.run(new AnalysisInput(packageInputs.asJava, Set.empty.asJava, plan))
    assertEquals(1, result.size())
    result.get(0).asInstanceOf[ComponentDependenciesResult].acd
  }

  private def prepareConstraints() = Constraints.builder().packageCoupling(PackageCouplingConstraints.builder().acd(new ACD()).build()).build()
}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 