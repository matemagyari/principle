package org.tindalos.principle.domain.core

class PackageReferenceScala(private val _name: String) { //extends Comparable[PackageReferenceScala] {
  
  def name() = _name
  def startsWith(str: String) = name.startsWith(str)

  def createChild(relativeNameOfDirectSubPackage: String) = name + "." + relativeNameOfDirectSubPackage

  def isDirectParentOf(reference: PackageReference) =
    if (this.equals(reference)) false
    else !reference.relativeNameTo(this.asInstanceOf[PackageReference]).contains(".")

  def isNotAnAncestorOf(reference: PackageReference) = !reference.pointsInside(this.asInstanceOf[PackageReference])

  def child(relativeName: String) = new PackageReference(name + "." + relativeName)

  def pointsInside(reference: PackageReference) = startsWith(reference.name + ".")

  def pointsToThatOrInside(reference: PackageReference) = this.equals(reference) || pointsInside(reference)

  def oneContainsAnother(that: PackageReference) = pointsToThatOrInside(that) || that.pointsToThatOrInside(this.asInstanceOf[PackageReference])

  def isDescendantOf(reference: PackageReference) = startsWith(reference.name + ".")

  /*
  def relativeNameTo(reference: PackageReference) =
    if (!this.name.startsWith(reference.name + ".")) {
      throw new IllegalArgumentException(this + " is not under " + reference);
    } else
      name.replaceFirst(reference.name + ".", "")

  def firstPartOfRelativeNameTo(reference: PackageReference) = relativeNameTo(reference).split("\\.", 2).head

  override def toString() = name

  override def compareTo(that: PackageReference) = name.compareTo(that.name)
*/
}