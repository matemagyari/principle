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

    
  def convert[T](javaList:java.util.Collection[T]):scala.collection.immutable.List[T] = {
    val mut = scala.collection.mutable.ListBuffer[T]()
    for(elem <- javaList) {
      mut.+=(elem)
    }
    mut.toList
  }
  
  def convert[T](scalaList:scala.collection.immutable.List[T]):java.util.List[T] = scalaList
  
  def convert[T](scalaSet:scala.collection.immutable.Set[T]):java.util.Set[T] = scalaSet

  def convert[T](scalaSet:scala.collection.mutable.Set[T]):java.util.Set[T] = scalaSet
  
  def convert[T](javaSet:java.util.Set[T]):scala.collection.immutable.Set[T] = {
    val mut = scala.collection.mutable.ListBuffer[T]()
    for(elem <- javaSet) {
      mut.+=(elem)
    }
    mut.toSet
  }
  
  val javaL = Lists.newArrayList(1, 2, 3)
  val scalaL = convert(javaL)
  println(scalaL)

}