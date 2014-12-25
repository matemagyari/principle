package org.tindalos.principle

import org.tindalos.principle.domain.detector.structure.PackageCohesionModule
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner

/**
 * Created by mate.magyari on 17/12/2014.
 */
object StructureTestManual extends App {

  val (targetDir, rootPackage) =
    ("//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-player-reputation-system/target/classes/"
      , "gamesys.poker.reputation")

  /*
  val (targetDir, rootPackage) =
    ("//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-critical-core/target/classes/"
      , "gamesys.poker.engine.model")

*/
  val classes = MyJDependRunner.createNodesOfClasses(rootPackage, targetDir)
  val packages = PackageCohesionModule.componentsFromPackages(rootPackage, classes)

  /*
  val x = packages
    .toList
    .sortBy(_._2.cohesion())


  packages
    .toList
    .sortBy(_._2.cohesion())
    .foreach {
    line => println(line._2.generalCohesion + "/" + line._2.internalCohesion + " " + line._1)
  }

  println("By name")
  packages
    .toList
    .sortBy(_._1)
    .foreach {
    line => println(line._2.generalCohesion + "/" + line._2.internalCohesion + " " + line._1)
  }
  */
  //packages.toList.sortBy(_.)
  val components = MyJDependRunner.findComponents(rootPackage, targetDir)

  //val x = components.foreach { _.cohesion() }
  println(components.toList.sortBy(_.nodes.size).reverse)
  println("hey")
  println(components.toList.sortBy(_.cohesion()).reverse)
  println("end")

}
