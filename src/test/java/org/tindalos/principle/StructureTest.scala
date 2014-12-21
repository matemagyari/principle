package org.tindalos.principle

import org.junit.Assert._
import org.junit.Test
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner

/**
 * Created by mate.magyari on 17/12/2014.
 */
class StructureTest {


  @Test
  def comp() {

    val components = MyJDependRunner.findComponents("gamesys.poker.reputation.domain").toList.sortBy(_.nodes.size).reverse

    println(components)
  }
}
