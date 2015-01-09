package org.tindalos.principle.infrastructure.reporters.packagestructure

import java.io.PrintWriter

import org.tindalos.principle.domain.agents.structure.Graph.{Peninsula, Node, SubgraphDecomposition}
import org.tindalos.principle.domain.agents.structure.Structure.NodeGroup
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter._


object PackageStructureHints2FileWriter {

  private val description = "The algorithm finds the classes in the codebase (vertices in the graph) that \"isolate\" some classes from the rest. Only the \"top\" class is referred anywhere else," +
    " meaning that the group could be \"collapsed\" into the top class without any external change needed. This gives some good package structuring hints (e.g. a group can be put in one package with" +
    " package private visibility for all classes but the \"top\"). A group can be a subset of another." +
    " \"Islands\", groups of interconnected classes having no external upstream or downstream dependencies, are also identified." +
    "\nThe cohesion value is also calculated for these groups."

  def writeToFile(subgraphDecomposition: SubgraphDecomposition) = {

    val printWriter = new PrintWriter(reportDirectory + "/" + packageStructureHints2FileName)
      .append(graphDescription + "\n\n")
      .append(description + "\n\n")
      .append(subSectionLine + "\n\n")

    def printPeninsula(p:Peninsula) {
        val firstLine =  s"\nCohesion: ${NodeGroup(p.subgraph).cohesion()}"
        if (p.island)
          printWriter.append(s"${firstLine} - This is an island\n")
        else
          printWriter.append(s"${firstLine}\n")

        p.frontNodes.map(_.id).toList.sorted.foreach {
          nodeId => printWriter.append(s"Top class: ${nodeId}\n")
        }
        printWriter.append(subSectionLine + "\n")
        (p.subgraph -- p.frontNodes).map(_.id).toList.sorted.foreach {
          nodeId => printWriter.append(s"           ${nodeId}\n")
        }
        printWriter.append("\n")
      }

    subgraphDecomposition
      .peninsulas
      .toList
      .sortBy(_.subgraph.size)
      .reverse
      .foreach(printPeninsula)

    printWriter.close()
  }

}
