package org.tindalos.principle.infrastructure.reporters.packagestructure

import java.io.PrintWriter

import org.tindalos.principle.domain.analyzers.structure.GroupingResult
import org.tindalos.principle.infrastructure.reporters.ReportsDirectoryManager
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionAnalysisResultReporter._
import scala.collection.JavaConverters._


object PackageStructureHints1FileWriter {

  private val description = "In the first step the algorithm finds the \"source\" vertices in the graph, the classes that are on the top of the dependency hierarchy (no other class refers to them)." +
    "The second step groups all the classes in the graph based on sources as upstream dependencies. This gives some good package structuring hints." +
    " E.g." +
    "\n\nSources: s01, s02" +
    "\n\torg.home.sample.Person" +
    "\n\torg.home.sample.Role" +
    "\n\nmeans the Person and Role classes are downstream dependencies of s01 and s02 but not of the other sources."

  def writeToFile(grouping: GroupingResult) = {

    val printWriter = new PrintWriter(ReportsDirectoryManager.reportDirectoryPath+"/"+packageStructureHints1FileName)

    printWriter
      .append(graphDescription+"\n\n")
      .append(description+"\n\n")
      .append(s"\nSources (${grouping.labelledSources().size()})\n")
      .append(subSectionLine+"\n\n")

    grouping.labelledSources().asScala.sortBy(_.label()).foreach {
      x => printWriter.append(x.label() + " -> " + x.nodeId()+"\n")
    }
    printWriter
      .append(s"\nGroups (${grouping.grouping().size()}) ordered by size\n")
      .append(subSectionLine+"\n\n")

    grouping.grouping().entrySet().asScala.toList.sortBy(_.getValue.size()).foreach {
      entry => {
        val sources = entry.getKey.asScala.foldLeft("")(_ + "," + _).substring(1)
        printWriter.append(s"Sources: ${sources}\n")
        entry.getValue.asScala.sorted.foreach {
          x => printWriter.append("\t" + x+"\n")
        }
      }
    }

    printWriter.close()
  }

}
