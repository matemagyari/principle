package org.tindalos.principle.domain.agents.structure

import org.junit.Assert._
import org.junit.Test

/**
 * Created by mate.magyari on 24/12/2014.
 */
class PackageGroupingModuleTest {

  @Test
  def getPackageNames() {
    assertEquals(
      PackageCohesionModule.getPackageNames("aa.bb", "aa.bb.cc.dd.ee")
      , Set("aa.bb.cc", "aa.bb.cc.dd", "aa.bb.cc.dd.ee"))
  }

}
