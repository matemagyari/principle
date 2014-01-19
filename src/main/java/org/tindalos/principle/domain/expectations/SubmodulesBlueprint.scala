package org.tindalos.principle.domain.expectations

class SubmodulesBlueprint(val submodulesDefinitionLocation:SubmodulesDefinitionLocation, val threshold:Integer) 
extends Thresholder(threshold) {
  
  def this() = this(null,0)

  val location = if (submodulesDefinitionLocation != null) submodulesDefinitionLocation.filePath() else null
}