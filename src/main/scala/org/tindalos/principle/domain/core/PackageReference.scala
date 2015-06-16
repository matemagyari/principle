package org.tindalos.principle.domain.core

import org.apache.commons.lang3.builder.HashCodeBuilder

class PackageReference(val name: String) extends Comparable[PackageReference] {
  
  def startsWith(str: String) = name.startsWith(str)

  def createChild(relativeNameOfDirectSubPackage: String) = name + "." + relativeNameOfDirectSubPackage

  def isDirectParentOf(reference: PackageReference) =
    if (this.equals(reference)) false
    else !reference.relativeNameTo(this).contains(".")

  def isNotAnAncestorOf(reference: PackageReference) = !reference.pointsInside(this)

  def child(relativeName: String) = new PackageReference(name + "." + relativeName)

  def pointsInside(reference: PackageReference) = startsWith(reference.name + ".")

  def pointsToThatOrInside(reference: PackageReference) = this.equals(reference) || pointsInside(reference)

  def oneContainsAnother(that: PackageReference) = pointsToThatOrInside(that) || that.pointsToThatOrInside(this)

  def isDescendantOf(reference: PackageReference) = startsWith(reference.name + ".")

  def relativeNameTo(reference: PackageReference) =
    if (!this.name.startsWith(reference.name + ".")) {
      throw new IllegalArgumentException(this + " is not under " + reference);
    } else
      name.replaceFirst(reference.name + ".", "")

  def firstPartOfRelativeNameTo(reference: PackageReference) = relativeNameTo(reference).split("\\.", 2).head

  override def toString() = name
  
  override def equals(other:Any) = if (!other.isInstanceOf[PackageReference]) false else other.asInstanceOf[PackageReference].name.equals(this.name)
  
  override def hashCode() = new HashCodeBuilder().append(name).hashCode()

  override def compareTo(that: PackageReference) = name.compareTo(that.name)
}