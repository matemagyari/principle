package org.tindalos.principle.domain.agents.structure

import org.tindalos.principle.domain.agents.structure.Graph.{NodeId, Node}
import org.tindalos.principle.domain.agents.structure.Structure.{NodeGroup}

import scala.annotation.tailrec

import scala.collection.immutable.Seq

object PackageCohesionModule {

  type PackageName = String

  case class CPackage(name: PackageName, component: NodeGroup)

  def packageOf(nodeId:NodeId) = nodeId.substring(0, nodeId.lastIndexOf('.'))

  def nodesInPackage(nodes:Set[Node], packageName: PackageName) =
    nodes.filter(n ⇒ PackageCohesionModule.packageOf(n.id).equals(packageName))

  val groupByPackages = (rootPackage: PackageName, nodes: Set[Node]) ⇒ {
    //packages with direct classes
    val endPackageNames = nodes.map(n ⇒ packageOf(n.id))
    val packageNames = endPackageNames.flatMap(p ⇒ getPackageNames(rootPackage, p))
    def findNodesInPackageRecursively(packageName: PackageName) = nodes.filter(n ⇒ n.id.startsWith(packageName))
    def buildPackage(pn: PackageName) = (pn, findNodesInPackageRecursively(pn))
    packageNames.map(buildPackage)
  }

  val componentsFromPackages = (rootPackage: PackageName, ns: Set[Node]) ⇒
    groupByPackages(rootPackage, ns)
      .map(x ⇒ (x._1, NodeGroup(x._2)))

  def packageCohesions(rootPackage: PackageName, ns: Set[Node]): Seq[(PackageName, NodeGroup, Double)] =
    componentsFromPackages(rootPackage, ns)
      .filter(x ⇒ x._2.nodes.size > 1)
      .map(x ⇒ (x._1, x._2, x._2.cohesion()))
      .toList.sortBy(_._3).reverse

  //returns all the package names under ...
  def getPackageNames(rootPackage: PackageName, p: PackageName):Set[PackageName] = {
    @tailrec
    def f(acc: Set[PackageName], pn: PackageName): Set[PackageName] =
      if (rootPackage.equals(pn)) acc
      else f(acc + pn, pn.substring(0, pn.lastIndexOf('.')))
    f(Set(),p)
  }
}
