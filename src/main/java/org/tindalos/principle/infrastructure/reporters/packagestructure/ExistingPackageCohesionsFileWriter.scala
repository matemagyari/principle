package org.tindalos.principle.infrastructure.reporters.packagestructure

import java.io.PrintWriter

import org.tindalos.principle.domain.agents.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.agents.structure.Structure.NodeGroup
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter.{reportDirectory, packageCohesionsFileName, generalDescription, round, sectionLine, subSectionLine}


object ExistingPackageCohesionsFileWriter {

  def writeToFile(result: CohesionAnalysisResult) = {

    val columns = "| Cohesion | Size | Upstream/Downstream dependencies of package | Internal/External edges of the classes |\n"
    val oneElementPackageDesc = "Each of the following packages contains only one class, therefore no cohesion is calculated"

    val printWriter = new PrintWriter(reportDirectory+"/"+packageCohesionsFileName)

    printWriter
      .append(generalDescription)
      .append("\n\n" + sectionLine + "\n")
      .append("Package cohesions - existing packages are listed, ordered by cohesion")
      .append("\n" + sectionLine + "\n\n")
      .append(columns)

    result.packages
      .filter(_._2.nodes.size > 1)
      .toList
      .sortBy(_._2.cohesion()).reverse
      .foreach {
      p => printWriter.append(s"\n ${groupToLine(p._2)}\t${p._1}")
    }

    printWriter.append("\n"+sectionLine+"\n")
    val minPackages = result.packages.filter(_._2.nodes.size == 1)
    printWriter.append(s"\n${oneElementPackageDesc} (${minPackages.size})\n\n")
    minPackages.toList.map(_._2.nodes.head.id).sorted.foreach {
      nodeId => printWriter.append( nodeId + "\n")
    }

    printWriter.close()
  }

  private def groupToLine(n: NodeGroup) =
    s"${round(n.cohesion())}\t" +
      s"| ${n.nodes.size}\t" +
      s"| ${n.externalDependants.size}/${n.externalDependencies.size}\t" +
      s"| ${n.internalArcsNo}/${n.externalArcsNo} | "

}
