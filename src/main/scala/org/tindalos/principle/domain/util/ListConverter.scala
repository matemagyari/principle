package org.tindalos.principle.domain.util

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import java.util.{List ⇒ JList}
import java.util.{Map ⇒ JMap}
import java.util.{Set ⇒ JSet}

object ListConverter extends App {

  def convert(javaMap:java.util.LinkedHashMap[String, JList[String]]):Map[String, List[String]] = {
    var scalaMap = Map[String, List[String]]()
    javaMap.foreach({ keyVal =>
      scalaMap += (keyVal._1 -> keyVal._2.asScala.to[List])
    })
    scalaMap    
  }

}