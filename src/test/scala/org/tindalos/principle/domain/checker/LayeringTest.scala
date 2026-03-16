package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.AnalysisInput
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.analyzers.layering.{LayerReference, LayerViolationsResult}
import org.tindalos.principle.domain.constraints._
import org.tindalos.principle.domain.core.packages.PackageWithMetrics
import org.tindalos.principle.infrastructure.JDependBasedPackageListBuilder
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer

import scala.collection.JavaConverters._

class LayeringTest {

  var plan: AnalysisPlan = null
  var analysisRunner = PoorMansDIContainer.buildAnalysisRunner()
  var expectations: Constraints = prepareChecks()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  @Test
  def simple() = {

    val result = run("org.tindalos.principletest.layering.simple")
    val expected = Set(new LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.app"),
      new LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.infrastructure"),
      new LayerReference("org.tindalos.principletest.layering.simple.app", "org.tindalos.principletest.layering.simple.infrastructure"))
    assertEquals(expected, result.asScala.toSet)
  }

  @Test
  def deeper() = {

    val result = run("org.tindalos.principletest.layering.deeper")
    val expected = Set(new LayerReference("org.tindalos.principletest.layering.deeper.domain.aaa", "org.tindalos.principletest.layering.deeper.app.bbb.ccc"))
    assertEquals(expected, result.asScala.toSet)
  }

  def init(basePackage: String) = {
    plan = new AnalysisPlan(expectations, basePackage)
  }

  private def run(basePackage: String) = {
    init(basePackage)
    val packageListProducer = new JDependBasedPackageListBuilder(basePackage)
    val packageList = packageListProducer.build()
    val result = analysisRunner.run(new AnalysisInput(packageList.map(p => p: PackageWithMetrics).asJava, Set.empty.asJava, plan))
    assertEquals(1, result.size())
    result.get(0).asInstanceOf[LayerViolationsResult].violations
  }

  private def prepareChecks() = Constraints.builder().layering(layering()).build()

  private def layering() = new Layering(java.util.List.of("infrastructure", "app", "domain"), 0)

}