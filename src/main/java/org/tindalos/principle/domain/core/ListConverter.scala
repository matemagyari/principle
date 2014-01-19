package org.tindalos.principle.domain.core

import scala.collection.JavaConversions._
import com.google.common.collect.Lists
import java.util.List
import scala.collection.mutable.ListBuffer
import scala.collection.immutable.List

object ListConverter extends App {

  def convert[T](javaList: java.util.List[T]): scala.collection.immutable.List[T] = {
    val mut = scala.collection.mutable.ListBuffer[T]()
    for (elem <- javaList) {
      mut.+=(elem)
    }
    mut.toList
  }

  def convert[T](javaList: java.util.Collection[T]): scala.collection.immutable.List[T] = {
    val mut = scala.collection.mutable.ListBuffer[T]()
    for (elem <- javaList) {
      mut.+=(elem)
    }
    mut.toList
  }

  def convertToMutable[T](javaList: java.util.Collection[T]): scala.collection.mutable.ListBuffer[T] = {
    val mut = scala.collection.mutable.ListBuffer[T]()
    for (elem <- javaList) {
      mut.+=(elem)
    }
    mut
  }

  def convert[T](scalaList: scala.collection.immutable.List[T]): java.util.List[T] = scalaList

  def convert[T](scalaSet: scala.collection.immutable.Set[T]): java.util.Set[T] = scalaSet
  def convert[T](scalaSet: scala.collection.mutable.Set[T]): java.util.Set[T] = scalaSet

  def convert[T](scalaMap: Map[PackageReference, Set[Cycle]]): java.util.Map[PackageReference, java.util.Set[Cycle]] = {
    val mut = new java.util.HashMap[PackageReference, java.util.Set[Cycle]]
    for ((k, v) <- scalaMap) {
      mut.put(k, ListConverter.convert(v))
    }
    mut
  }
  def convertMapSet[K, V](scalaMap: Map[K, Set[V]]): java.util.Map[K, java.util.Set[V]] = {
    val mut = new java.util.HashMap[K, java.util.Set[V]]
    for ((k, v) <- scalaMap) {
      mut.put(k, ListConverter.convert(v))
    }
    mut
  }
  def convertMapSet[K, V](javaMap: java.util.Map[K, java.util.Set[V]]): Map[K, Set[V]] = {
    val mut = scala.collection.mutable.Map[K, Set[V]]()
    for ((k, v) <- javaMap) {
      mut.put(k, ListConverter.convert(v))
    }
    mut.toMap
  }

  def convert[T](javaSet: java.util.Set[T]): scala.collection.immutable.Set[T] = {
    val mut = scala.collection.mutable.ListBuffer[T]()
    for (elem <- javaSet) {
      mut.+=(elem)
    }
    mut.toSet
  }

  def convert[K, V](javaMap: java.util.Map[K, V]): scala.collection.immutable.Map[K, V] = {
    var scalaMap = scala.collection.immutable.Map[K, V]()
    javaMap.foreach({ keyVal =>
      scalaMap += (keyVal._1 -> keyVal._2)
    })
    scalaMap
  }
  
  def convert(javaMap:java.util.LinkedHashMap[String, java.util.List[String]]):scala.collection.immutable.Map[String, scala.collection.immutable.List[String]] = {
    var scalaMap = scala.collection.immutable.Map[String, scala.collection.immutable.List[String]]()
    javaMap.foreach({ keyVal =>
      scalaMap += (keyVal._1 -> convert(keyVal._2))
    })
    scalaMap    
  }

}