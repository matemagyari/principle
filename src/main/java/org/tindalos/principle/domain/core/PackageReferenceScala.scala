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
  /*

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
*/
}