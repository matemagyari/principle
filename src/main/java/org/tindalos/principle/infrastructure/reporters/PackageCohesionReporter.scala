package org.tindalos.principle.infrastructure.reporters

import java.io.PrintWriter

import org.tindalos.principle.domain.detector.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.detector.structure.Structure.NodeGroup
import org.tindalos.principle.domain.resultprocessing.reporter.AnalysisResultsReporter

object PackageCohesionReporter {

  def report(result: CohesionAnalysisResult): AnalysisResultsReporter.Report = {

    val sectionLine = "=============================================================="
    val subSectionLine = "-------------------------------------------------------------"

    val generalDescription = """
        A directed graph is built representing the structure of the code, where the vertices represent the classes and
        the edges the relationships between them.
        Cohesion between a group of vertices (classes) is calculated by the C = 1 - E1 / E2 formula.
            -- E1 is the number of edges the vertices in the group participate in. This means 'internal' edges, where both ends of
               the edge is from the group and 'external' ones, where only one end is.
            -- E2 would be the number of edges belonging to the new vertex if the vertices in the group collapsed into one.
               So all internal edges would disappear and multiple external edges might collapse into each other as well.
            The cohesion measures how much relative decrease in the number of edges would a grouping of a given set of
            vertices cause. 0.0 means the collapsing wouldn't decrease the number of edges at all, while 1 means would be no edge left.
                             """.stripMargin

    val packagesDescription =
      "Existing packages are listed, ordered by cohesion. The next pair is the upstream/downstream dependencies of the package. " +
        "The last pair is internal/external dependencies of the classes.\n"


    val cohesiveGroupsDescription = "\n\nThe following groups are formed as a result of cohesion analysis\n"
    val columns = "| Cohesion | upstream/downstream dependencies | internal/external edges |\n"
    val orphanNodesDescription = "\nThe following classes did not fit into any cohesive group\n"
    val cohesionDetailsFileName = "cohesion_details.txt"

    val printWriter = new PrintWriter(cohesionDetailsFileName)

    printWriter
      .append("\n\n" + generalDescription)
      .append("\n" + sectionLine + "\n")
      .append("Package cohesions")
      .append("\n" + sectionLine + "\n")
      .append(packagesDescription + "\n")
      .append(columns)

    result.packages
      .toList
      .sortBy(_._2.cohesion()).reverse
      .foreach {
      p => printWriter.append("\n" + groupToLine(p._2) + "\t" + p._1)
    }

    printWriter
      .append("\n" + sectionLine + "\n")
      .append(cohesiveGroupsDescription)
      .append("\n" + sectionLine + "\n")
      .append("Cohesive groups")
      .append("\n" + sectionLine + "\n")
      .append("\n" + columns)



    result.cohesiveNodeGroups
      .toList.sortBy(_.cohesion()).reverse.filter(_.nodes.size > 1)
      .foreach {
      p => printWriter.append("\n" + groupToLine(p) + "\n" + listNodes(p) + "\n" + subSectionLine + "\n")
    }

    printWriter.append("\n"+sectionLine+"\n")
    val orphanNodes = result.cohesiveNodeGroups.filter(_.nodes.size == 1)
    printWriter.append(orphanNodesDescription+s" (${orphanNodes.size})\n")
    orphanNodes.toList.map(_.nodes.head.id).sorted.foreach {
      nodeId => printWriter.append( nodeId + "\n")
    }

    printWriter.close()

    val sb = new StringBuffer("\n" + sectionLine + "\n")
    sb.append("\tPackage Cohesion Analysis\t")
    sb.append("\n" + sectionLine + "\n")
    sb.append(s"\nFor details check file: ${cohesionDetailsFileName} \n\n")

    sb.append(sectionLine + "\n")

    sb.toString()
  }

  def round(d: Double) = BigDecimal(d).setScale(2, BigDecimal.RoundingMode.HALF_UP).toDouble

  def listNodes(n: NodeGroup) = n.nodes.toList.map(_.id).sorted.foldLeft("")(_ + "\n" + _)

  def groupToLine(p: NodeGroup) =
    s"${round(p.cohesion())} | ${p.externalDependants.size}/${p.externalDependencies.size} | ${p.internalArcsNo}/${p.externalArcsNo} | "

}