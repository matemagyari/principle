package org.tindalos.principle.app.service

import org.junit.Assert._
import org.junit.Test
import org.tindalos.principle.domain.core.AnalysisPlan
import org.tindalos.principle.domain.expectations._

import scala.collection.immutable.Seq

class InputValidatorTest {

  val basePackage: String = "xx"

  val aLayering = Layering(layers = Seq("a","b","c"))

  @Test
  def wrongOrder() {

    val barriers = Seq(Barrier("a"),Barrier("c"),Barrier("b"))
    val configuration: AnalysisPlan = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertFalse(success)
  }

  @Test
  def invalidBarrier() {

    val barriers = Seq(Barrier("a"),Barrier("d"))
    val configuration: AnalysisPlan = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertFalse(success)
  }


  @Test
  def fullCover() {

    val barriers = Seq(Barrier("a"),Barrier("b"),Barrier("c"))
    val configuration: AnalysisPlan = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertTrue(success)
  }

  @Test
  def partialCover() {

    val barriers = Seq(Barrier("a"), Barrier("c"))
    val configuration: AnalysisPlan = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertTrue(success)
  }


  private def config(barriers: Seq[Barrier]): AnalysisPlan = {
    val aThirdParty = ThirdParty(barriers)

    val expectations = new Checks(layering = aLayering, thirdParty = Some(aThirdParty))
    new AnalysisPlan(expectations, basePackage)
  }
}
