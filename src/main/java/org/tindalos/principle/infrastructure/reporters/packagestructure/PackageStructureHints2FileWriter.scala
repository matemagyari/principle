package org.tindalos.principle.infrastructure.reporters.packagestructure

import java.io.PrintWriter

import org.tindalos.principle.domain.detector.structure.Graph.Node
import org.tindalos.principle.domain.detector.structure.Structure.NodeGroup
import org.tindalos.principle.infrastructure.reporters.packagestructure.PackageCohesionReporter._


object PackageStructureHints2FileWriter {

  private val description = "This algorithm finds the classes in the codebase (vertices in the graph) that \"isolate\" some classes from the rest. Only the \"top\" class is referred anywhere else," +
    " meaning that the group could be \"collapsed\" into the top class without any external change needed. This gives some good package structuring hints." +
    "\nThe cohesion value is also calculated for these groups."

  def writeToFile(hints: List[(Node, Set[Node], Double)]) = {

    val printWriter = new PrintWriter(reportDirectory + "/" + packageStructureHints2FileName)
      .append(graphDescription + "\n\n")
      .append(description + "\n\n")
      .append(subSectionLine + "\n\n")

    hints.foreach {
      p => {
        printWriter.append("\nTop class: " + p._1.id + " " + p._3+"\n")
        p._2.map(_.id).toList.sorted.foreach {
          n => printWriter.append("\t" + n + "\n")
        }
      }
    }

    printWriter.close()
  }

}
