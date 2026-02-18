package org.tindalos.principle.domain.analyzers.submodulesblueprint

class SubmoduleDefinitions(val definitions: Map[SubmoduleId, SubmoduleDefinition]) {
  import scala.collection.JavaConverters._

  checkNoOverlaps(definitions.values.toList)

  private def checkNoOverlaps(definitions: List[SubmoduleDefinition]) {
    val overlaps = for (
      submoduleDefinition <- definitions; anOtherDefinition <- definitions.filterNot(_.equals(submoduleDefinition))
      if submoduleDefinition.overlapsWith(anOtherDefinition)
    ) yield new Overlap(submoduleDefinition.id, anOtherDefinition.id)
    if (!overlaps.isEmpty) throw new OverlappingSubmoduleDefinitionsException(overlaps.toSet.asJava)
  }

  def getPackages(submoduleId: SubmoduleId) = {
    definitions.get(submoduleId).get.packages().asScala.toSet
  }
}
