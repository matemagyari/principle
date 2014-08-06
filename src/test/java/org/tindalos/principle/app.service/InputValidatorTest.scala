package org.tindalos.principle.app.service

import org.junit.Assert._
import org.junit.Test
import org.tindalos.principle.domain.core.DesignQualityCheckConfiguration
import org.tindalos.principle.domain.expectations.{Barrier, ThirdParty, Layering, DesignQualityExpectations}
import org.tindalos.principle.infrastructure.plugin.Checks

/**
 * Created by mate.magyari on 03/08/2014.
 */
class InputValidatorTest {

  val basePackage: String = "xx"

  val aLayering = new Layering(layers = List("a","b","c"))

  val testObj = new InputValidator()

  @Test
  def wrongOrder() {

    val barriers = List(new Barrier("a"),new Barrier("c"),new Barrier("b"))
    val configuration: DesignQualityCheckConfiguration = config(barriers)

    val (success,msg) = testObj.validate(configuration)

    assertFalse(success)
  }

  @Test
  def invalidBarrier() {

    val barriers = List(new Barrier("a"),new Barrier("d"))
    val configuration: DesignQualityCheckConfiguration = config(barriers)

    val (success,msg) = testObj.validate(configuration)

    assertFalse(success)
  }


  @Test
  def fullCover() {

    val barriers = List(new Barrier("a"),new Barrier("b"),new Barrier("c"))
    val configuration: DesignQualityCheckConfiguration = config(barriers)

    val (success,msg) = testObj.validate(configuration)

    assertTrue(success)
  }

  @Test
  def partialCover() {

    val barriers = List(new Barrier("a"), new Barrier("c"))
    val configuration: DesignQualityCheckConfiguration = config(barriers)

    val (success,msg) = testObj.validate(configuration)

    assertTrue(success)
  }


  private def config(barriers: List[Barrier]): DesignQualityCheckConfiguration = {
    val aThirdParty = new ThirdParty(barriers)

    val expectations = new Checks(layering = aLayering, thirdParty = aThirdParty)
    new DesignQualityCheckConfiguration(expectations, basePackage)
  }
}
