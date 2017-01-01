package org.tindalos.principle.app.service

import org.junit.Assert._
import org.junit.Test
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.expectations.{Barrier, ThirdParty, Layering, Expectations}
import org.tindalos.principle.infrastructure.plugin.Checks

class InputValidatorTest {

  val basePackage: String = "xx"

  val aLayering = Layering(layers = List("a","b","c"))

  @Test
  def wrongOrder() {

    val barriers = List(new Barrier("a"),new Barrier("c"),new Barrier("b"))
    val configuration: AnalysisPlan = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertFalse(success)
  }

  @Test
  def invalidBarrier() {

    val barriers = List(new Barrier("a"),new Barrier("d"))
    val configuration: AnalysisPlan = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertFalse(success)
  }


  @Test
  def fullCover() {

    val barriers = List(new Barrier("a"),new Barrier("b"),new Barrier("c"))
    val configuration: AnalysisPlan = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertTrue(success)
  }

  @Test
  def partialCover() {

    val barriers = List(new Barrier("a"), new Barrier("c"))
    val configuration: AnalysisPlan = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertTrue(success)
  }


  private def config(barriers: List[Barrier]): AnalysisPlan = {
    val aThirdParty = new ThirdParty(barriers)

    val expectations = new Checks(layering = aLayering, thirdParty = Some(aThirdParty))
    new AnalysisPlan(expectations, basePackage)
  }
}
