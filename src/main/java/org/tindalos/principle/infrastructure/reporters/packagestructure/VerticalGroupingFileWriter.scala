package org.tindalos.principle.infrastructure.reporters.packagestructure

import java.io.PrintWriter

import org.tindalos.principle.domain.detector.structure.Graph.NodeId
import org.tindalos.principle.domain.detector.structure.PackageStructureFinder.GroupingResult
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter._


object VerticalGroupingFileWriter {

  def writeToFile(grouping: GroupingResult) = {

    val printWriter = new PrintWriter(reportDirectory+"/"+verticalGroupingFileName)

    printWriter
      .append(graphDescription+"\n\n")
      .append(s"\nSources (${grouping.labelledSources.size})\n")
      .append(subSectionLine+"\n\n")

    grouping.labelledSources.sortBy(_._1).foreach {
      x => printWriter.append(x._1 + " -> " + x._2+"\n")
    }
    printWriter
      .append(s"\nGroups (${grouping.grouping.size}) ordered by size\n")
      .append(subSectionLine+"\n\n")

    grouping.grouping.to[List].sortBy(_._2.size).foreach {
      kv => {
        val sources = kv._1.foldLeft("")(_ + "," + _).substring(1)
        printWriter.append(s"Sources: ${sources}\n")
        kv._2.sorted.foreach {
          x => printWriter.append("\t" + x+"\n")
        }
      }
    }

    printWriter.close()
  }

  def aSort(s1:(String,NodeId),s2:(String,NodeId)) = 1//s1.substring(1).toInt.compareTo(s2.substring(1).toInt)


}
