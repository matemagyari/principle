package org.tindalos.principle.domain.expectations.cumulativedependency

abstract class DoubleThresholder(var threshold: Double = 0)
class ACD(_threshold: Double = 0) extends DoubleThresholder(_threshold) {
  def this() = this(0)
}
class NCCD(_threshold: Double = 0) extends DoubleThresholder(_threshold) {
  def this() = this(0)
}
class RACD(_threshold: Double = 0) extends DoubleThresholder(_threshold) {
  def this() = this(0)
}
