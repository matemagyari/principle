package org.tindalos.principle

import org.tindalos.principle.domain.analyzers.structure.NodeGroup
import org.tindalos.principle.domain.analyzers.structure.{CohesiveGroupsDiscoveryModule, Graph, PackageCohesionModule, PackageStructureHints1Finder}
import org.tindalos.principle.infrastructure.service.jdepend.classdependencies.MyJDependRunner

import scala.collection.JavaConverters._

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
  val packages = PackageCohesionModule
    .componentsFromPackages(rootPackage, classes.asJava)
    .asScala
    .map(entry => (entry.getKey, entry.getValue))
    .toSet

  val sortedPackages = packages
    .filter(_._2.nodes.size > 1)
    .toList
    .sortBy(_._2.cohesion())


  val grouping = PackageStructureHints1Finder.makeGroups(classes)

  def aSort(s1: String, s2: String) = s1.substring(1).toInt.compareTo(s2.substring(1).toInt)

  println("\nSources:")
  grouping.labelledSources().asScala.sortBy(_.label()).foreach {
    x => println(x.label() + " -> " + x.nodeId())
  }
  println("\nGroups:")
  grouping.grouping().entrySet().asScala.foreach {
    entry => {
      println(entry.getKey.asScala.foldLeft("Sources: ")(_ + "," + _))
      entry.getValue.asScala.sorted.foreach {
        x => println("\t" + x)
      }
    }
  }

  var start = System.currentTimeMillis()

  val cohesionsForGrouping = for {
    entry <- grouping.grouping().entrySet().asScala
    nodeSet = entry.getValue.asScala.map(x => classes.find(n => n.id == x).get).toSet
    if nodeSet.size > 1
    nodeGroup = new NodeGroup(nodeSet.asJava)
  } yield (entry.getKey, nodeGroup.cohesion())

  cohesionsForGrouping.toList.sortBy(_._2).reverse.foreach {
    c => println(c._2 + " " + c._1)
  }

  //val parts = Graph.findDetachableSubgraphs(classes)
  val parts = Graph.findDetachableSubgraphs(MyJDependRunner.createNodesOfClasses("org.tindalos.principle.infrastructure").asJava)

  parts.peninsulas.asScala.foreach {
    p => {
      println("Top: " + p.frontNodes + " " + new NodeGroup(p.subgraph).cohesion())
      p.subgraph.asScala.map(_.id).toList.sorted.foreach {
        n => println("\t"+n)
      }
    }
  }

  val initialComponents = classes.map(n => new NodeGroup(java.util.Collections.singleton(n)))

  start = System.currentTimeMillis()
  val components = CohesiveGroupsDiscoveryModule.collapseToLimit(initialComponents).toList.sortBy(_.nodes.size).reverse
  println("Time1: " + (System.currentTimeMillis() - start))

  val componentsSortedBySize = components.toList.sortBy(_.nodes.size).reverse
  val componentsSortedByCohesion = components.toList.sortBy(_.cohesion()).reverse

  println("end")

}
