package org.tindalos.principle.domain.checker

import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.junit.Test
import org.junit._
import org.tindalos.principle.infrastructure.plugin.Checks
import org.tindalos.principle.domain.detector.adp._
import org.tindalos.principle.domain.expectations._
import org.tindalos.principle.domain.expectations.cumulativedependency._
import org.junit.Assert.assertEquals
import org.tindalos.principle.domain.core.Cycle
import org.tindalos.principle.domain.core.PackageReference

class ADPTest {

  var designQualityCheckConfiguration: DesignQualityCheckConfiguration = null
  var designQualityCheckService: DesignQualityCheckService = null
  var checks: DesignQualityExpectations = prepareChecks()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  @Test
  def simple() = {
    val result = run("org.tindalos.principletest.cycle.simple")
    val expectedCycle = new Cycle(new PackageReference("org.tindalos.principletest.cycle.simple.left"), new PackageReference("org.tindalos.principletest.cycle.simple.right"))
    val expected = Map(new PackageReference("org.tindalos.principletest.cycle.simple.left") -> Set(expectedCycle))
    assertEquals(expected, result)
  }

  @Test
  def transitive() = {

    val result = run("org.tindalos.principletest.cycle.transitive")
    val expectedCycle = new Cycle(
      new PackageReference("org.tindalos.principletest.cycle.transitive.a"),
      new PackageReference("org.tindalos.principletest.cycle.transitive.b"),
      new PackageReference("org.tindalos.principletest.cycle.transitive.c"))
    val expected = Map(new PackageReference("org.tindalos.principletest.cycle.transitive.a") -> Set(expectedCycle))
    assertEquals(expected, result)
  }

  @Test
  def btwParentAndChild() = {

    val result = run("org.tindalos.principletest.cycle.btwparentandchild");
    val expectedCycle = new Cycle(
      new PackageReference("org.tindalos.principletest.cycle.btwparentandchild"),
      new PackageReference("org.tindalos.principletest.cycle.btwparentandchild.child"))
    val expected = Map(new PackageReference("org.tindalos.principletest.cycle.btwparentandchild") -> Set(expectedCycle))
    assertEquals(expected, result)
  }

  @Test
  def complex1() = {

    val result = run("org.tindalos.principletest.cycle.complex1");
    val expectedCycle = new Cycle(
      new PackageReference("org.tindalos.principletest.cycle.complex1.left"),
      new PackageReference("org.tindalos.principletest.cycle.complex1.right"))
    val expected = Map(new PackageReference("org.tindalos.principletest.cycle.complex1.right") -> Set(expectedCycle))
    assertEquals(expected, result)
  }

  @Test
  def complex2() = {

    val result = run("org.tindalos.principletest.cycle.complex2");
    val expectedCycle = new Cycle(
      new PackageReference("org.tindalos.principletest.cycle.complex2.left"),
      new PackageReference("org.tindalos.principletest.cycle.complex2.right.right"))
    val expected = Map(new PackageReference("org.tindalos.principletest.cycle.complex2.right.right") -> Set(expectedCycle))
    assertEquals(expected, result)
  }

  def init(basePackage: String) = {
    designQualityCheckService = PoorMansDIContainer.getDesignCheckService(basePackage)
    designQualityCheckConfiguration = new DesignQualityCheckConfiguration(checks, basePackage)
  }

  private def run(basePackage: String) = {
    init(basePackage)
    val result = designQualityCheckService.analyze(designQualityCheckConfiguration)
    assertEquals(1, result.checkResults.length)
    result.checkResults.head.asInstanceOf[ADPResult].cyclesByBreakingPoints
  }

  private def prepareChecks() = new Checks(packageCoupling())

  private def packageCoupling() = {
    val packageCoupling = new PackageCoupling()
    packageCoupling.adp = new ADP()
    packageCoupling
  }

}