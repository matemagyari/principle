package org.tindalos.principle.domain.detector.structure

import org.tindalos.principle.domain.detector.structure.Structure.{NodeGroup, Node}

/**
 * Created by mate.magyari on 21/12/2014.
 */
object PackageCohesionModule {

  type PackageName = String

  case class CPackage(name: PackageName, component: NodeGroup)

  def groupByPackages(nodes: Set[Node]): Set[(PackageName, Set[Node])] = {
    val packageNames = nodes.map(n => n.id.substring(0, n.id.lastIndexOf('.')))
    def findNodesInPackageRecursively(packageName: PackageName) = nodes.filter(n => n.id.startsWith(packageName))
    def buildPackage(pn: PackageName) = (pn, findNodesInPackageRecursively(pn))
    packageNames.map(buildPackage)
  }

  def componentsFromPackages(ns: Set[Node]) = groupByPackages(ns).map(x => (x._1, NodeGroup(x._2)))

  def packageCohesions(ns: Set[Node]): List[(PackageName, NodeGroup, Double)] =
    componentsFromPackages(ns)
      .filter(x => x._2.nodes.size > 1)
      .map(x => (x._1, x._2, x._2.cohesion()))
      .toList.sortBy(_._3).reverse
}
