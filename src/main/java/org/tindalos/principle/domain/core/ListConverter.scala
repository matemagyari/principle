package org.tindalos.principle.domain.core

import scala.collection.JavaConversions._
import com.google.common.collect.Lists
import java.util.List
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.List

object ListConverter extends App {
  
  def convert[T](javaList:java.util.List[T]):scala.collection.immutable.List[T] = {
    val mut = scala.collection.mutable.ListBuffer[T]()
    for(elem <- javaList) {
      mut.+=(elem)
    }
    mut.toList
  }
  
  val javaL = Lists.newArrayList(1, 2, 3)
  val scalaL = convert(javaL)
  println(scalaL)

}