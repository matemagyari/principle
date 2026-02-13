package org.tindalos.principle.app.service

import org.junit.Assert._
import org.junit.Test
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.expectations._

class InputValidatorTest {

  val basePackage: String = "xx"

  val aLayering = new Layering(java.util.List.of("a","b","c"), 0)

  @Test
  def wrongOrder() {

    val barriers = List(Barrier.of("a"),Barrier.of("c"),Barrier.of("b"))
    val configuration: AnalysisPlan = config(barriers)

    val result = InputValidator.validate(configuration)

    assertFalse(result.success)
  }

  @Test
  def invalidBarrier() {

    val barriers = List(Barrier.of("a"),Barrier.of("d"))
    val configuration: AnalysisPlan = config(barriers)

    val result = InputValidator.validate(configuration)

    assertFalse(result.success)
  }


  @Test
  def fullCover() {

    val barriers = List(Barrier.of("a"),Barrier.of("b"),Barrier.of("c"))
    val configuration: AnalysisPlan = config(barriers)

    val result = InputValidator.validate(configuration)

    assertTrue(result.success)
  }

  @Test
  def partialCover() {

    val barriers = List(Barrier.of("a"), Barrier.of("c"))
    val configuration: AnalysisPlan = config(barriers)

    val result = InputValidator.validate(configuration)

    assertTrue(result.success)
  }


  private def config(barriers: List[Barrier]): AnalysisPlan = {
    val aThirdParty = ThirdParty(barriers)

    val expectations = new Checks(layering = aLayering, thirdParty = Some(aThirdParty))
    new AnalysisPlan(expectations, basePackage)
  }
}
