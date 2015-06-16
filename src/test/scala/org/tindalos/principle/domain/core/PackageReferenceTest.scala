package org.tindalos.principle.domain.core

import org.junit.Test

import org.junit.Assert._

class PackageReferenceTest {

  private val testObj = ref("aa.bb.cc")

  @Test
  def child() = {
    assertMatches("aa.bb.cc.dd", testObj.child("dd"))
  }
  @Test
  def createChild() = {
    assertEquals("aa.bb.cc.dd", testObj.createChild("dd"))
  }

  @Test
  def firstPartOfRelativeNameTo() = {
    assertEquals("bb", testObj.firstPartOfRelativeNameTo(ref("aa")))
  }
  @Test
  def isDescendantOf() = {
    assertTrue(testObj.isDescendantOf(ref("aa.bb")))
    assertTrue(testObj.isDescendantOf(ref("aa")))
    assertFalse(testObj.isDescendantOf(ref("aa.bbb")))
    assertFalse(testObj.isDescendantOf(testObj))
    assertFalse(testObj.isDescendantOf(ref("aa.bb.cc.dd")))
    assertFalse(testObj.isDescendantOf(ref("aa.bb.ccc")))
  }
  @Test
  def isDirectParentOf() = {
    assertTrue(testObj.isDirectParentOf(ref("aa.bb.cc.dd")))
    assertFalse(testObj.isDirectParentOf(ref("aa.bb.cc.dd.ee")))
    assertFalse(testObj.isDirectParentOf(testObj))
  }

  @Test
  def isNotAnAncestorOf() = {
    assertTrue(testObj.isNotAnAncestorOf(ref("aa.bb.ccc")))
    assertTrue(testObj.isNotAnAncestorOf(ref("xxxx")))
    assertTrue(testObj.isNotAnAncestorOf(testObj))
    assertTrue(testObj.isNotAnAncestorOf(ref("aa")))

    assertFalse(testObj.isNotAnAncestorOf(ref("aa.bb.cc.dd.ee")))
    assertFalse(testObj.isNotAnAncestorOf(ref("aa.bb.cc.dd")))

  }

  @Test
  def oneContainsAnother() = {
    assertFalse(testObj.oneContainsAnother(ref("aa.bb.ccc")))
    assertFalse(ref("aa.bb.ccc").oneContainsAnother(testObj))

    assertTrue(testObj.oneContainsAnother(ref("aa")))
    assertTrue(testObj.oneContainsAnother(ref("aa.bb")))
    assertTrue(testObj.oneContainsAnother(ref("aa.bb.cc.dd")))
    assertTrue(ref("aa").oneContainsAnother(testObj))
    assertTrue(ref("aa.bb").oneContainsAnother(testObj))
    assertTrue(ref("aa.bb.cc.dd").oneContainsAnother(testObj))
    assertTrue(testObj.oneContainsAnother(testObj))
  }

  @Test
  def pointsInside() = {
    assertTrue(testObj.pointsInside(ref("aa")))
    assertTrue(testObj.pointsInside(ref("aa.bb")))

    assertFalse(testObj.pointsInside(testObj))
    assertFalse(testObj.pointsInside(ref("aa.bbb")))
  }

  @Test
  def pointsToThatOrInside() = {
    assertTrue(testObj.pointsToThatOrInside(ref("aa")))
    assertTrue(testObj.pointsToThatOrInside(ref("aa.bb")))

    assertTrue(testObj.pointsToThatOrInside(testObj))
    assertFalse(testObj.pointsToThatOrInside(ref("aa.bbb")))
  }

  @Test
  def relativeNameTo() = {
    assertEquals("bb.cc", testObj.relativeNameTo(ref("aa")))
    assertEquals("cc", testObj.relativeNameTo(ref("aa.bb")))
  }

  @Test(expected = classOf[IllegalArgumentException])
  def invalidRelativeNameTo():Unit = {
    testObj.relativeNameTo(ref("aa.b"))
  }

  @Test
  def startsWith() = {
    assertTrue(testObj.startsWith("aa"))
    assertTrue(testObj.startsWith("aa."))
    assertTrue(testObj.startsWith("aa.bb"))
    assertTrue(testObj.startsWith("aa.bb.cc"))
    assertFalse(testObj.startsWith("aa.bb.cc."))
    assertFalse(testObj.startsWith("aa.bb.dd"))
    assertFalse(testObj.startsWith("xxxx"))
  }

  @Test
  def compareTo() = {

    assertEquals(0, ref("aa.bb").compareTo(ref("aa.bb")))
    assertEquals(-1, ref("aa.bb.cc").compareTo(ref("aa.bb.dd")))
    assertEquals(1, ref("aa.bb.dd").compareTo(ref("aa.bb.cc")))

  }

  private def assertMatches(expected: String, actual: PackageReference): Unit = {
    assertEquals(ref(expected), actual)
  }

  private def ref(str: String) = new PackageReference(str)

}