package org.tindalos.principle.infrastructure.reporters.packagestructure

import java.io.PrintWriter

import org.tindalos.principle.domain.agents.structure.Structure.NodeGroup
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter.{reportDirectory, cohesiveGroupsFileName, generalDescription, round, sectionLine, subSectionLine}

object CohesiveGroupsFileWriter {

  def writeToFile(cohesiveNodeGroups: Set[NodeGroup]) = {
    val cohesiveGroupsDescription = "The following groups are formed as a result of cohesion analysis"

    val orphanNodesDescription = "\nThe following classes did not fit into any cohesive group"

    val printWriter = new PrintWriter(reportDirectory+"/"+cohesiveGroupsFileName)

    printWriter
      .append(generalDescription+"\n\n")
      .append(cohesiveGroupsDescription)
      .append("\n" + sectionLine + "\n\n")

    cohesiveNodeGroups
      .toList.sortBy(_.cohesion()).reverse.filter(_.nodes.size > 1)
      .foreach {
      p => printWriter.append(s"\n${groupToLine2(p)} \n${listNodes(p)} \n${subSectionLine}\n")
    }

    printWriter.append("\n"+sectionLine+"\n")
    val orphanNodes = cohesiveNodeGroups.filter(_.nodes.size == 1)
    printWriter.append(s"${orphanNodesDescription} (${orphanNodes.size})\n\n")
    orphanNodes.toList.map(_.nodes.head.id).sorted.foreach {
      nodeId => printWriter.append( nodeId + "\n")
    }

    printWriter.close()
  }

  private def listNodes(n: NodeGroup) = n.nodes.toList.map(_.id).sorted.foldLeft("")(_ + "\n" + _)

  private def groupToLine2(n: NodeGroup) =
    s"Cohesion: ${round(n.cohesion())} " +
      s"| Size: ${n.nodes.size} " +
      s"| Upstream/Downstream dependencies of the group : ${n.externalDependants.size}/${n.externalDependencies.size} " +
      s"| Internal/External edges of the classes: ${n.internalArcsNo}/${n.externalArcsNo} | "

}
