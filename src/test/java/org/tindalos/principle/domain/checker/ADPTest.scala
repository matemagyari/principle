package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.core.{Cycle, AnalysisPlan, PackageReference}
import org.tindalos.principle.domain.coredetector.{AnalysisInput, AnalysisResult}
import org.tindalos.principle.domain.detector.adp._
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.plugin.Checks

class ADPTest {

  var plan: AnalysisPlan = null
  var runAnalysis = PoorMansDIContainer.buildRunAnalysisFn()
  var checks: Expectations = prepareChecks()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  @Test
  def simple() = {
    val result = run("org.tindalos.principletest.cycle.simple")
    val expectedCycle = new Cycle(ref("org.tindalos.principletest.cycle.simple.left"), ref("org.tindalos.principletest.cycle.simple.right"))
    val expected = Map(ref("org.tindalos.principletest.cycle.simple.left") -> Set(expectedCycle))
    assertEquals(expected, result)
  }

  @Test
  def transitive() = {

    val result = run("org.tindalos.principletest.cycle.transitive")
    val expectedCycle = new Cycle(
      ref("org.tindalos.principletest.cycle.transitive.a"),
      ref("org.tindalos.principletest.cycle.transitive.b"),
      ref("org.tindalos.principletest.cycle.transitive.c"))
    val expected = Map(ref("org.tindalos.principletest.cycle.transitive.a") -> Set(expectedCycle))
    assertEquals(expected, result)
  }
  
  @Test
  def transitive2() = {

    val result = run("org.tindalos.principletest.cycle.transitive2")
    val expectedCycle = new Cycle(
      ref("org.tindalos.principletest.cycle.transitive2.a"),
      ref("org.tindalos.principletest.cycle.transitive2.b"),
      ref("org.tindalos.principletest.cycle.transitive2.c"))
    val expected = Map(ref("org.tindalos.principletest.cycle.transitive2.c") -> Set(expectedCycle))
    assertEquals(expected, result)
  }

  @Test
  def btwParentAndChild() = {

    val result = run("org.tindalos.principletest.cycle.btwparentandchild")
    val expectedCycle = new Cycle(
      ref("org.tindalos.principletest.cycle.btwparentandchild"),
      ref("org.tindalos.principletest.cycle.btwparentandchild.child"))
    val expected = Map(ref("org.tindalos.principletest.cycle.btwparentandchild") -> Set(expectedCycle))
    assertEquals(expected, result)
  }

  @Test
  def complex1() = {

    val result = run("org.tindalos.principletest.cycle.complex1")
    val expectedCycle = new Cycle(
      ref("org.tindalos.principletest.cycle.complex1.left"),
      ref("org.tindalos.principletest.cycle.complex1.right"))
    val expected = Map(ref("org.tindalos.principletest.cycle.complex1.right") -> Set(expectedCycle))
    assertEquals(expected, result)
  }

  @Test
  def complex2() = {

    val result = run("org.tindalos.principletest.cycle.complex2")
    val expectedCycle = new Cycle(
      ref("org.tindalos.principletest.cycle.complex2.left"),
      ref("org.tindalos.principletest.cycle.complex2.right.right"))
    val expected = Map(ref("org.tindalos.principletest.cycle.complex2.right.right") -> Set(expectedCycle))
    assertEquals(expected, result)
  }

  def init(basePackage: String) = {
    plan = new AnalysisPlan(checks, basePackage)
  }

  private def run(basePackage: String) = {
    init(basePackage)
    val packageListProducer = PoorMansDIContainer.buildPackageListProducerFn(basePackage)
    val packageList = packageListProducer(basePackage)
    val result = runAnalysis(new AnalysisInput(packageList, plan))
    assertEquals(1, result.length)
    result.head.asInstanceOf[ADPResult].cyclesByBreakingPoints
  }

  private def prepareChecks() = new Checks(packageCoupling())

  private def packageCoupling() = {
    val packageCoupling = new PackageCoupling()
    packageCoupling.adp = new ADP()
    packageCoupling
  }
  
  private def ref(reference:String) = new PackageReference(reference)

}