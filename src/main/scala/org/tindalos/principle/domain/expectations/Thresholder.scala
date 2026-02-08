package org.tindalos.principle.domain.expectations

abstract class Thresholder(val violationsThreshold:Int)

case class ADP(override val violationsThreshold:Int = 0) extends Thresholder(violationsThreshold)
case class SDP(override val violationsThreshold:Int = 0) extends Thresholder(violationsThreshold)
case class SAP(override val violationsThreshold:Int = 0, maxDistance:Double = 0.0) extends Thresholder(violationsThreshold)

case class Layering(layers: List[String] = List.empty, override val violationsThreshold: Int = 0) extends Thresholder(violationsThreshold)


case class Barrier(layer: String = "", components: String = "") {

  def componentList() =
    if (components.isEmpty) List()
    else components
        .filter(_ >= ' ') //remove whitespaces
        .split(",").to[List].map(_.trim)
}

case class ThirdParty(barriers: List[Barrier] = List.empty, override val violationsThreshold: Int = 0) extends Thresholder(violationsThreshold)

case class SubmodulesBlueprint(location:String, override val violationsThreshold:Int = 0) extends Thresholder(violationsThreshold)

