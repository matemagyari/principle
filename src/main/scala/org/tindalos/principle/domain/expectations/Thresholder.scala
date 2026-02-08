package org.tindalos.principle.domain.expectations


case class Barrier(layer: String = "", components: String = "") {

  def componentList() =
    if (components.isEmpty) List()
    else components
        .filter(_ >= ' ') //remove whitespaces
        .split(",").to[List].map(_.trim)
}

case class ThirdParty(barriers: List[Barrier] = List.empty, override val violationThreshold: Int = 0) extends IntThresholder

case class SubmodulesBlueprint(location:String, override val violationThreshold:Int = 0) extends IntThresholder

