package org.tindalos.principle.domain.detector.structure

/**
 * Created by mate.magyari on 21/12/2014.
 */
object Structure {

  type NodeId = String

  case class Node(name: NodeId, dependencies: Set[NodeId], dependants: Set[NodeId])

  case class Component(nodes: Set[NodeId], dependencies: Set[NodeId], dependants: Set[NodeId]) {
    def merge(c: Component) = {
      val mergedNodes = nodes ++ c.nodes
      val mergedDependencies = dependencies ++ c.dependencies -- mergedNodes
      val mergedDependants = dependants ++ c.dependants -- mergedNodes
      Component(mergedNodes, mergedDependencies, mergedDependants)
    }

    def commonDependenciesRatio(c: Component) =
      if (dependencies.isEmpty)
        if (c.dependencies.isEmpty) 1.0
        else 0.0
      else (dependencies & c.dependencies).size.toDouble / dependencies.size.toDouble

    def commonDependantsRatio(c: Component) =
      if (dependants.isEmpty)
        if (c.dependants.isEmpty) 1.0
        else 0.0
      else (dependants & c.dependants).size.toDouble / dependants.size.toDouble

    //how many of classes of 'c' this component depends on
    def connectionRate(c: Component) = (dependencies & c.nodes).size.toDouble / c.nodes.size.toDouble

    def gravityTo(c: Component) = {
      //if (isConnectedTo(c) || c.isConnectedTo(this))
      commonDependenciesRatio(c) * commonDependantsRatio(c)
      //else 0.0
    }

    def isConnectedTo(c: Component) = !((dependencies & c.nodes).isEmpty)

    //cohesion of a component: in [0,1). 0.0 - no cohesion
    def cohesion(allNodes: Set[Node]) = {
      val componentDepsCount = dependencies.size + dependants.size
      val classesInComponent = allNodes.filter(c => nodes.contains(c.name)).toList
      val classesDepsCount =
        classesInComponent.map(c => (c.dependencies.size + c.dependants.size)).sum

      if (classesDepsCount == 0) 0.0
      else 1 - componentDepsCount.toDouble / classesDepsCount.toDouble
    }

    def cohesionDelta(c: Component, allClasses: Set[Node]) = {
      val myCohesion = this.cohesion(allClasses)
      val otherCohesion = c.cohesion(allClasses)
      val merged = merge(c)
      val mergedCohesion = merged.cohesion(allClasses)
      mergedCohesion - Math.max(myCohesion, otherCohesion)
    }

    override def toString() = nodes.toString()
  }

}
