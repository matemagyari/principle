package org.tindalos.principle.domain.expectations

abstract class Thresholder(var violationsThreshold:Integer = 0) {
  
}

class ADP(threshold:Integer = 0) extends Thresholder(threshold) {
  def this() = this(0)
}
class SDP(threshold:Integer = 0) extends Thresholder(threshold){
  def this() = this(0)
}
class SAP(threshold:Integer = 0, var maxDistance:Double = 0) extends Thresholder(threshold) {
  def this() = this(0)
}

