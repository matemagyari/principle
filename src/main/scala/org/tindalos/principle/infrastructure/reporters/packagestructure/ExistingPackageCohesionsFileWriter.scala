package org.tindalos.principle.infrastructure.reporters.packagestructure

import java.io.PrintWriter

import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult
import org.tindalos.principle.domain.analyzers.structure.NodeGroup
import scala.collection.JavaConverters._
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionAnalysisResultReporter.{packageCohesionsFileName, generalDescription, round}
import PackageCohesionConstants._

object ExistingPackageCohesionsFileWriter {

  def writeToFile(result: CohesionAnalysisResult) = {

    val columns = "| Cohesion | Size | Upstream/Downstream dependencies of package | Internal/External edges of the classes |\n"
    val oneElementPackageDesc = "Each of the following packages contains only one class, therefore no cohesion is calculated"

    val printWriter = new PrintWriter(ReportsDirectoryManager.reportDirectoryPath+"/"+packageCohesionsFileName)

    printWriter
      .append(generalDescription)
      .append("\n\n" + SECTION_LINE + "\n")
      .append("Package cohesions - existing packages are listed, ordered by cohesion")
      .append("\n" + SECTION_LINE + "\n\n")
      .append(columns)

    result.packages()
      .entrySet().asScala
      .filter(_.getValue.nodes.size > 1)
      .toList
      .sortBy(_.getValue.cohesion()).reverse
      .foreach {
      e => printWriter.append(s"\n ${groupToLine(e.getValue)}\t${e.getKey}")
    }

    printWriter.append("\n"+SECTION_LINE+"\n")
    val minPackages = result.packages().entrySet().asScala.filter(_.getValue.nodes.size == 1)
    printWriter.append(s"\n${oneElementPackageDesc} (${minPackages.size})\n\n")
    minPackages.toList.map(_.getValue.nodes.asScala.head.id).sorted.foreach {
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
