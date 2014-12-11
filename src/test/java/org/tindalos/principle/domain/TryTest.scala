package org.tindalos.principle.domain

import scala.util.{Success, Failure, Try}

/**
 * Created by mate.magyari on 11/12/2014.
 */
object TryTest extends App {

  def div5(a: Int) = Try(100 / a)

  val result = div5(2)

  val r = result match {
    case Success(rs) => rs
    case Failure(ex: ArithmeticException) => 5
    case Failure(ex: RuntimeException) => 3
  }
  println(result)
  println(r)

  val x = for {
    a <- div5(0)
    b <- div5(a)
  } yield b

  println(x)

}
