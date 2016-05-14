package org.tindalos.principle.domain.expectations

trait DoubleThresholder {
  val threshold: Double = 0
}
case class ACD(override val threshold: Double = 0) extends DoubleThresholder
case class NCCD(override val threshold: Double = 0) extends DoubleThresholder
case class RACD(override val threshold: Double = 0) extends DoubleThresholder


abstract class Thresholder(val violationsThreshold:Int = 0)

case class ADP(threshold:Int = 0) extends Thresholder(threshold)
case class SDP(threshold:Int = 0) extends Thresholder(threshold)
case class SAP(threshold:Int = 0, maxDistance:Double = 0.0) extends Thresholder(threshold)

case class Layering(layers: List[String] = List.empty, threshold: Int = 0) extends Thresholder


case class Barrier(layer: String = "", components: String = "") {

  def componentList() =
    if (components.isEmpty) List()
    else components
        .filter(_ >= ' ') //remove whitespaces
        .split(",").to[List].map(_.trim)
}

case class ThirdParty(barriers: List[Barrier] = List.empty, threshold: Int = 0) extends Thresholder

case class SubmodulesBlueprint(location:String, threshold:Int = 0) extends Thresholder(threshold)

