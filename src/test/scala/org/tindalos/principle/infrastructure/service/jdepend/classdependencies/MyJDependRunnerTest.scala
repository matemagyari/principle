package org.tindalos.principle.infrastructure.service.jdepend.classdependencies

import org.junit.Assert._
import org.junit.Test

class MyJDependRunnerTest {

  @Test
  def className() {
    assertEquals("aaa.MyClass",MyJDependRunner.className("aaa.MyClass"))
    assertEquals("aaa.MyClass",MyJDependRunner.className("aaa.MyClass$InnerClass"))
    assertEquals("aaa.MyClass",MyJDependRunner.className("aaa.MyClass$InnerClass$Again"))
  }

  @Test
  def xxx2() {
    val x = MyJDependRunner.createNodesOfClasses("org.tindalos.principle")
  }

}
