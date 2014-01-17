package org.tindalos.principle.domain.detector.submodulesblueprint

class SubmoduleDefinitions(val definitions: Map[SubmoduleId, SubmoduleDefinition]) {

  checkNoOverlaps(definitions.values.toList)

  private def checkNoOverlaps(definitions: List[SubmoduleDefinition]) {

    val overlaps = for (
      submoduleDefinition <- definitions; anOtherDefinition <- definitions.filterNot(_.equals(submoduleDefinition));
      if submoduleDefinition.overlapsWith(anOtherDefinition)
    ) yield new Overlap(submoduleDefinition.id, anOtherDefinition.id)

    if (!overlaps.isEmpty) throw new OverlappingSubmoduleDefinitionsException(overlaps.toSet)
  }
  /*
	public SubmoduleDefinitionsOld(List[SubmoduleDefinition] theDefinitions) {
		this.definitions = Maps.newHashMap()
		for (SubmoduleDefinition submoduleDefinition : theDefinitions) {
			definitions.put(submoduleDefinition.id(), submoduleDefinition)
		}
	}
*/
  def getPackages(submoduleId: SubmoduleId) = definitions.get(submoduleId).get.packages

}

case class Overlap(val submodule1: SubmoduleId, val submodule2: SubmoduleId) {
	val submoduleIds = Set(submodule1, submodule2)
}