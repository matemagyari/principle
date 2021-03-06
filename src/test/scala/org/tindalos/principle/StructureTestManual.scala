package org.tindalos.principle

import org.tindalos.principle.domain.agents.structure.Structure.NodeGroup
import org.tindalos.principle.domain.agents.structure.{CohesiveGroupsDiscoveryModule, Graph, PackageCohesionModule, PackageStructureHints1Finder}
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner

/**
 * Created by mate.magyari on 17/12/2014.
 */
object StructureTestManual extends App {

  val (targetDir, rootPackage) =
    ("//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-critical-core/target/classes/"
      , "gamesys.poker.engine.model")

  /*
  val (targetDir, rootPackage) =
    ("//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-player-reputation-system/target/classes/"
      , "gamesys.poker.reputation")



      val (targetDir, rootPackage) =
        ("//Users/mate.magyari/IdeaProjects/gamesys/gamesplatform/poker-game-system/target/classes/"
          , "gamesys.poker.engine")

  */


  val classes = MyJDependRunner.createNodesOfClasses(rootPackage, targetDir)
  val packages = PackageCohesionModule.componentsFromPackages(rootPackage, classes)

  val sortedPackages = packages
    .filter(_._2.nodes.size > 1)
    .toList
    .sortBy(_._2.cohesion())


  val grouping = PackageStructureHints1Finder.makeGroups(classes)

  def aSort(s1: String, s2: String) = s1.substring(1).toInt.compareTo(s2.substring(1).toInt)

  println("\nSources:")
  grouping.labelledSources.sortBy(_._1).foreach {
    x => println(x)
  }
  println("\nGroups:")
  grouping.grouping.foreach {
    kv => {
      println(kv._1.foldLeft("Sources: ")(_ + "," + _))
      kv._2.sorted.foreach {
        x => println("\t" + x)
      }
    }
  }

  var start = System.currentTimeMillis()

  val cohesionsForGrouping = for {
    g <- grouping.grouping
    nodeSet = g._2.map(x => classes.find(n => n.id == x).get).toSet
    if nodeSet.size > 1
    nodeGroup = NodeGroup(nodeSet)
  } yield (g._1, nodeGroup.cohesion())

  cohesionsForGrouping.toList.sortBy(_._2).reverse.foreach {
    c => println(c._2 + " " + c._1)
  }

  //val parts = Graph.findDetachableSubgraphs(classes)
  val parts = Graph.findDetachableSubgraphs(MyJDependRunner.createNodesOfClasses("org.tindalos.principle.infrastructure"))

  parts.peninsulas.foreach {
    p => {
      println("Top: " + p.frontNodes + " " + NodeGroup(p.subgraph).cohesion())
      p.subgraph.map(_.id).toList.sorted.foreach {
        n => println("\t"+n)
      }
    }
  }

  val initialComponents = classes.map(n => NodeGroup(Set(n)))

  start = System.currentTimeMillis()
  val components = CohesiveGroupsDiscoveryModule.collapseToLimit(initialComponents).toList.sortBy(_.nodes.size).reverse
  println("Time1: " + (System.currentTimeMillis() - start))

  val componentsSortedBySize = components.toList.sortBy(_.nodes.size).reverse
  val componentsSortedByCohesion = components.toList.sortBy(_.cohesion()).reverse

  println("end")

}
