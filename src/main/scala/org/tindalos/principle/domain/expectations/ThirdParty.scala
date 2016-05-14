package org.tindalos.principle.domain.expectations

case class Barrier(layer: String = "", components: String = "") {

  def componentList() =
    if (components.isEmpty) List()
    else components
      .filter(_ >= ' ') //remove whitespaces
      .split(",").to[List].map(_.trim)
}

case class ThirdParty(barriers: List[Barrier] = List.empty, threshold: Int = 0) extends Thresholder