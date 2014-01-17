package org.tindalos.principle.domain.detector.submodulesblueprint

import scala.collection.JavaConversions.asScalaSet
import scala.collection.JavaConversions.setAsJavaSet

import org.tindalos.principle.domain.core.ListConverter

class OverlappingSubmoduleDefinitionsException(val overlaps: Set[Overlap])
  extends InvalidBlueprintDefinitionException(OverlappingSubmoduleDefinitionsException.toMessage(overlaps)) 

object OverlappingSubmoduleDefinitionsException {

  def toMessage(overlaps: java.util.Set[Overlap]) = {

    val msg = new StringBuffer("Overlapping submodules: ")
    overlaps.foreach({ overlap =>
      msg.append("\n")
      overlap.submoduleIds.foreach({ submoduleId => msg.append(submoduleId + " and ") })
      msg.append(msg.substring(0, msg.length() - 4))

    })

    msg.toString()
  }

}