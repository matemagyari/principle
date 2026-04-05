error id: file://<WORKSPACE>/src/test/scala/org/tindalos/principle/infrastructure/service/jdepend/classdependencies/MyJDependRunnerTest.scala:MyJDependRunner
file://<WORKSPACE>/src/test/scala/org/tindalos/principle/infrastructure/service/jdepend/classdependencies/MyJDependRunnerTest.scala
empty definition using pc, found symbol in pc: 
semanticdb not found

found definition using fallback; symbol MyJDependRunner
offset: 489
uri: file://<WORKSPACE>/src/test/scala/org/tindalos/principle/infrastructure/service/jdepend/classdependencies/MyJDependRunnerTest.scala
text:
```scala
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
    val x = MyJDependRunner@@.createNodesOfClasses("org.tindalos.principle")
  }

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 