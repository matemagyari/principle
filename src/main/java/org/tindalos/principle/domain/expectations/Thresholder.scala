package org.tindalos.principle.domain.expectations

abstract class Thresholder(val violationsThreshold:Int = 0)

class ADP(threshold:Int = 0) extends Thresholder(threshold) {
  def this() = this(0)
}
class SDP(threshold:Int = 0) extends Thresholder(threshold){
  def this() = this(0)
}
class SAP(threshold:Int = 0, val maxDistance:Double = 0) extends Thresholder(threshold) {
  def this() = this(0)
}

