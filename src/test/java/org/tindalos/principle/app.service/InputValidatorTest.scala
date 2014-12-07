package org.tindalos.principle.app.service

import org.junit.Assert._
import org.junit.Test
import org.tindalos.principle.domain.core.ExpectationsConfig
import org.tindalos.principle.domain.expectations.{Barrier, ThirdParty, Layering, Expectations}
import org.tindalos.principle.infrastructure.plugin.Checks

/**
 * Created by mate.magyari on 03/08/2014.
 */
class InputValidatorTest {

  val basePackage: String = "xx"

  val aLayering = new Layering(layers = List("a","b","c"))

  @Test
  def wrongOrder() {

    val barriers = List(new Barrier("a"),new Barrier("c"),new Barrier("b"))
    val configuration: ExpectationsConfig = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertFalse(success)
  }

  @Test
  def invalidBarrier() {

    val barriers = List(new Barrier("a"),new Barrier("d"))
    val configuration: ExpectationsConfig = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertFalse(success)
  }


  @Test
  def fullCover() {

    val barriers = List(new Barrier("a"),new Barrier("b"),new Barrier("c"))
    val configuration: ExpectationsConfig = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertTrue(success)
  }

  @Test
  def partialCover() {

    val barriers = List(new Barrier("a"), new Barrier("c"))
    val configuration: ExpectationsConfig = config(barriers)

    val (success,msg) = InputValidator.validate(configuration)

    assertTrue(success)
  }


  private def config(barriers: List[Barrier]): ExpectationsConfig = {
    val aThirdParty = new ThirdParty(barriers)

    val expectations = new Checks(layering = aLayering, thirdParty = aThirdParty)
    new ExpectationsConfig(expectations, basePackage)
  }
}
