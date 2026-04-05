file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/analyzers/structure/PackageGroupingModuleTest.scala
empty definition using pc, found symbol in pc: 
semanticdb not found
empty definition using fallback
non-local guesses:
	 -org/junit/Assert.PackageCohesionModule.getPackageNames.
	 -org/junit/Assert.PackageCohesionModule.getPackageNames#
	 -org/junit/Assert.PackageCohesionModule.getPackageNames().
	 -scala/collection/JavaConverters.PackageCohesionModule.getPackageNames.
	 -scala/collection/JavaConverters.PackageCohesionModule.getPackageNames#
	 -scala/collection/JavaConverters.PackageCohesionModule.getPackageNames().
	 -PackageCohesionModule.getPackageNames.
	 -PackageCohesionModule.getPackageNames#
	 -PackageCohesionModule.getPackageNames().
	 -scala/Predef.PackageCohesionModule.getPackageNames.
	 -scala/Predef.PackageCohesionModule.getPackageNames#
	 -scala/Predef.PackageCohesionModule.getPackageNames().
offset: 320
uri: file://<WORKSPACE>/src/test/scala/org/tindalos/principle/domain/analyzers/structure/PackageGroupingModuleTest.scala
text:
```scala
package org.tindalos.principle.domain.analyzers.structure

import org.junit.Assert._
import org.junit.Test
import scala.collection.JavaConverters._

/**
 * Created by mate.magyari on 24/12/2014.
 */
class PackageGroupingModuleTest {

  @Test
  def getPackageNames() {
    assertEquals(
      PackageCohesionModule.getPac@@kageNames("aa.bb", "aa.bb.cc.dd.ee").asScala.toSet
      , Set("aa.bb.cc", "aa.bb.cc.dd", "aa.bb.cc.dd.ee"))
  }

}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 