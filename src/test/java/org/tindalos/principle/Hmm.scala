package org.tindalos.principle

/**
 * Created by mate.magyari on 06/01/2015.
 */
object Hmm extends App {

  def add(a:Int,b:Int) = a + b

  val add3 = add(_:Int,3)

  def curryAdd(a:Int)(b:Int) = a + b
  val curryAdd3 = curryAdd(3)_

  val t:PartialFunction[Int, Int] = {
    case 4 => 5
    case 8 => 8
  }

  val t2:PartialFunction[Int, Any] = {
    case _ => "No!"
  }


  val r = t orElse t2
  println(r(5))
  println(add3(6))
  println(curryAdd(6)(4))
  println(curryAdd3(5))
}
