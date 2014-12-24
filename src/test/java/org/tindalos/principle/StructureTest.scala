package org.tindalos.principle

import org.junit.Test
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner

/**
 * Created by mate.magyari on 17/12/2014.
 */
class StructureTest {

  @Test
  def comp() {

    val (targetDir, rootPackage) =
      ("//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-player-reputation-system/target/classes/"
        , "gamesys.poker.reputation")

    /*
    val (targetDir, rootPackage) =
      ("//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-critical-core/target/classes/"
        , "gamesys.poker.engine.model")

    val components = MyJDependRunner.findComponents(rootPackage, targetDir)

    //val x = components.foreach { _.cohesion() }
    println(components.toList.sortBy(_.nodes.size).reverse)
    println("hey")
    println(components.toList.sortBy(_.cohesion()).reverse)
    println("end")
*/

  }
}
