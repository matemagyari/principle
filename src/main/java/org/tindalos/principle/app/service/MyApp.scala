package org.tindalos.principle.app.service

object MyApp extends App {
  
  val x = Some("Bela")
  
  val li = List(1,2)
  
  
  println(li.map(_*2))
  //x.flatMap(x => x.+x)

  println("Hello Zsolti")

  println(List(Some(1), None, Some(2)).flatten)

}