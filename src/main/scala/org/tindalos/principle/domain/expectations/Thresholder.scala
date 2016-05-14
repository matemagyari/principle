package org.tindalos.principle.domain.expectations

abstract class Thresholder(val violationsThreshold:Int = 0)

case class ADP(threshold:Int = 0) extends Thresholder(threshold)
case class SDP(threshold:Int = 0) extends Thresholder(threshold)
case class SAP(threshold:Int = 0, maxDistance:Double = 0.0) extends Thresholder(threshold)

