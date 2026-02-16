package org.tindalos.principle.domain.core

import org.apache.commons.lang3.builder.HashCodeBuilder

import scala.collection.JavaConverters._

class Cycle(val references: List[PackageReference]) extends Comparable[Cycle] {
  
  //var x:org.tindalos.principle.domain.expectations.Checks = null
  def this(refs:PackageReference*) = this(refs.toList)
  def this(refs:java.util.List[PackageReference]) = this(refs.asScala.toList)

  if (references == null || references.length < 2) throw new DomainException("Invalid cycle " + references)

  val end = references.last
  
  override def toString() = {
    val arrow = "-->"
    val sb = new StringBuffer("*" + arrow)
    references foreach { reference =>
      sb.append(reference + arrow)
    }
    sb.append("*").toString()
  }

  override def equals(other: Any) = other match {
    case castOther: Cycle =>
      if (!references.toSet.equals(castOther.references.toSet)) false
      else {

        val offset = castOther.references.indexOf(references.head)
        var result = true
        for (i <- 0 to references.length-1 if result) {
          val indexWithOffset = (i + offset) % references.length
          if (!references(i).equals(castOther.references(indexWithOffset))) {
            result = false
          }
        }
        result
      }
    case _ => false
  }

  override def hashCode() = new HashCodeBuilder().append(references.length).hashCode()
  
  override def compareTo(that:Cycle) = this.toString().compareTo(that.toString())

}