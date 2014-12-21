package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.detector.structure.Structure.{Component, Node}

/**
 * Created by mate.magyari on 21/12/2014.
 */
object PackageCohesionModule {

  type PackageName = String

  case class CPackage(name: PackageName, component: Component)

  def groupByPackages(nodes: Set[Node]): Set[(PackageName, Set[Node])] = {
    val packageNames = nodes.map(n => n.name.substring(0, n.name.lastIndexOf('.')))
    def findNodesInPackageRecursively(packageName: PackageName) = nodes.filter(n => n.name.startsWith(packageName))
    def buildPackage(pn: PackageName) = (pn, findNodesInPackageRecursively(pn))
    packageNames.map(buildPackage)
  }

  def componentsFromPackages(ns: Set[Node]) = groupByPackages(ns).map(x => (x._1, StructureFinder.nodesToComponent(x._2)))

  def packageCohesions(ns: Set[Node]): List[(PackageName, Component, Double)] =
    componentsFromPackages(ns)
      .map(x => (x._1, x._2, x._2.cohesion(ns)))
      .toList.sortBy(_._3).reverse
}
