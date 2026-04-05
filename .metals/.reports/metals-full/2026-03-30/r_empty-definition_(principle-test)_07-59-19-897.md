file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ADPTest.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -org/junit/packageList/asScala.
	 -org/tindalos/principle/domain/analyzers/adp/packageList/asScala.
	 -org/tindalos/principle/domain/constraints/packageList/asScala.
	 -scala/collection/JavaConverters.packageList.asScala.
	 -packageList/asScala.
	 -scala/Predef.packageList.asScala.
offset: 4235
uri: file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/checker/ADPTest.scala
text:
```scala
package org.tindalos.principle.domain.checker

import org.junit.Assert.assertEquals
import org.junit._
import org.tindalos.principle.domain.plan.AnalysisInput
import org.tindalos.principle.domain.core.{AnalysisPlan, Cycle}
import org.tindalos.principle.domain.analyzers.adp._
import org.tindalos.principle.domain.constraints._
import org.tindalos.principle.domain.core.packages.{PackageReference, PackageWithMetrics}
import org.tindalos.principle.infrastructure.service.jdepend.JDependBasedPackageListBuilder
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner

import java.util
import java.util.Optional
import scala.collection.JavaConverters._

class ADPTest {

  var plan: AnalysisPlan = null
  var analysisRunner = PoorMansDIContainer.buildAnalysisRunner()
  val checks = Constraints.builder().packageCoupling(PackageCouplingConstraints.builder().adp(new ADP()).build()).build()

  @Before
  def setup() = {
    TestFixture.setLogger()
  }

  @Test
  def simple() = {
    val result = run("org.tindalos.principletest.cycle.simple")
    val expectedCycle = new Cycle(ref("org.tindalos.principletest.cycle.simple.left"), ref("org.tindalos.principletest.cycle.simple.right"))
    val expected = Map(ref("org.tindalos.principletest.cycle.simple.right") -> Set(expectedCycle).asJava).asJava
    assertEquals(expected, result)
  }

  @Test
  def transitive() = {
    val result = run("org.tindalos.principletest.cycle.transitive")
    val expectedCycle = new Cycle(
      ref("org.tindalos.principletest.cycle.transitive.a"),
      ref("org.tindalos.principletest.cycle.transitive.b"),
      ref("org.tindalos.principletest.cycle.transitive.c"))
    val expected = Map(ref("org.tindalos.principletest.cycle.transitive.c") -> Set(expectedCycle).asJava).asJava
    assertEquals(expected, result)
  }
  
  @Test
  def transitive2() = {
    val result = run("org.tindalos.principletest.cycle.transitive2")
    val expectedCycle = new Cycle(
      ref("org.tindalos.principletest.cycle.transitive2.a"),
      ref("org.tindalos.principletest.cycle.transitive2.b"),
      ref("org.tindalos.principletest.cycle.transitive2.c"))
    assertEquals(1, result.size())
    assertEquals(Optional.of(ref("org.tindalos.principletest.cycle.transitive2")), result.keySet().iterator().next().parent()) 
    val expectedValue: util.Set[Cycle] = result.values().iterator().next()
    assertEquals(util.Collections.singleton(expectedCycle), expectedValue)
  }

  @Test
  def btwParentAndChild() = {
    val result = run("org.tindalos.principletest.cycle.btwparentandchild")
    val expectedCycle = new Cycle(
      ref("org.tindalos.principletest.cycle.btwparentandchild"),
      ref("org.tindalos.principletest.cycle.btwparentandchild.child"))
    val expected = Map(ref("org.tindalos.principletest.cycle.btwparentandchild.child") -> Set(expectedCycle).asJava).asJava
    assertEquals(expected, result)
  }

  @Test
  def complex1() = {
    val result = run("org.tindalos.principletest.cycle.complex1")
    val expectedCycle = new Cycle(
      ref("org.tindalos.principletest.cycle.complex1.left"),
      ref("org.tindalos.principletest.cycle.complex1.right"))
    val expected = Map(ref("org.tindalos.principletest.cycle.complex1.right") -> Set(expectedCycle).asJava).asJava
    assertEquals(expected, result)
  }

  @Test
  def complex2() = {
    val result = run("org.tindalos.principletest.cycle.complex2")
    val expectedCycle = new Cycle(
      ref("org.tindalos.principletest.cycle.complex2.left"),
      ref("org.tindalos.principletest.cycle.complex2.right.right"))
    val expected = Map(ref("org.tindalos.principletest.cycle.complex2.right.right") -> Set(expectedCycle).asJava).asJava
    assertEquals(expected, result)
  }

  def init(basePackage: String) = {
    plan = new AnalysisPlan(checks, basePackage)
  }

  private def run(basePackage: String): util.Map[PackageReference, util.Set[Cycle]] = {
    init(basePackage)
    val packageListProducer = new JDependBasedPackageListBuilder(basePackage)
    val packageList = packageListProducer.build()
    val classes = MyJDependRunner.createNodesOfClasses(basePackage)
    val packageInputs = packageList.a@@sScala.map(p => p: PackageWithMetrics).toList
    val result = analysisRunner.run(new AnalysisInput(packageInputs.asJava, classes.asJava, plan))
    assertEquals(1, result.size())
    result.get(0).asInstanceOf[ADPResult].cyclesByBreakingPoints
  }

  private def ref(reference:String) = new PackageReference(reference)

}
```


#### Short summary: 

empty definition using pc, found symbol in pc: 