package org.tindalos.principle.domain.detector.structure

import org.junit.Assert._
import org.junit.Test

class PackageStructureFinderTest {

  @Test
  def merge2Maps() {
    val m1 = Map("a"->1, "b"->2)
    val m2 = Map("b"->3, "c"->4)
    def mySum(xs:List[Int]) = xs.sum
    val r = PackageStructureFinder.merge(mySum,m1,m2)
    val r2 = PackageStructureFinder.merge(mySum,List(m1,m2))

    assertEquals(Map("a"->1, "b"->5, "c"->4),r2)

  }
}
