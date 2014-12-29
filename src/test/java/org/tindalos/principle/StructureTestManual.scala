package org.tindalos.principle

import org.tindalos.principle.domain.detector.structure.{PackageStructureFinder, Graph, PackageCohesionModule}
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner

/**
 * Created by mate.magyari on 17/12/2014.
 */
object StructureTestManual extends App {


  /*
     val (targetDir, rootPackage) =
       ("//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-player-reputation-system/target/classes/"
         , "gamesys.poker.reputation")

   */

      val (targetDir, rootPackage) =
        ("//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-critical-core/target/classes/"
          , "gamesys.poker.engine.model")

  val classes = MyJDependRunner.createNodesOfClasses(rootPackage, targetDir)
  val packages = PackageCohesionModule.componentsFromPackages(rootPackage, classes)

  val sortedPackages = packages
    .filter(_._2.nodes.size > 1)
    .toList
    .sortBy(_._2.cohesion())

  val sources = Graph.findSources(classes)

  val validGraph = Graph.isValid(classes)

  val grouping = PackageStructureFinder.makeGroups(classes)

  grouping.foreach {
    kv => {
      println(kv._1)
      kv._2.sorted.foreach {
        x => println("\t" + x)
      }
    }
  }

  val components = MyJDependRunner.findComponents(rootPackage, targetDir)

  val componentsSortedBySize = components.toList.sortBy(_.nodes.size).reverse
  val componentsSortedByCohesion = components.toList.sortBy(_.cohesion()).reverse

  println("end")

}
