package org.tindalos.principle.domain.core

case class PackageReferenceScala(val name: String) extends Comparable[PackageReferenceScala] {

  def startsWith(str: String) = name.startsWith(str)

  def createChild(relativeNameOfDirectSubPackage: String) = name + "." + relativeNameOfDirectSubPackage

  def isDirectParentOf(reference: PackageReferenceScala) =
    if (this.equals(reference)) false
    else !reference.relativeNameTo(PackageReferenceScala.this).contains(".")

  def isNotAnAncestorOf(reference: PackageReferenceScala) = !reference.pointsInside(this)

  def child(relativeName: String) = new PackageReferenceScala(name + "." + relativeName)

  def pointsInside(reference: PackageReferenceScala) = startsWith(reference.name + ".")

  def pointsToThatOrInside(reference: PackageReferenceScala) = PackageReferenceScala.this.equals(reference) || pointsInside(reference)

  def oneContainsAnother(that: PackageReferenceScala) = pointsToThatOrInside(that) || that.pointsToThatOrInside(PackageReferenceScala.this)

  def isDescendantOf(reference: PackageReferenceScala) = startsWith(reference.name + ".")

  def relativeNameTo(reference: PackageReferenceScala) =
    if (!this.name.startsWith(reference.name + ".")) {
      throw new IllegalArgumentException(this + " is not under " + reference);
    } else
      name.replaceFirst(reference.name + ".", "")

  def firstPartOfRelativeNameTo(reference: PackageReferenceScala) = relativeNameTo(reference).split("\\.", 2).head

  override def toString() = name

  override def compareTo(that: PackageReferenceScala) = name.compareTo(that.name)

}