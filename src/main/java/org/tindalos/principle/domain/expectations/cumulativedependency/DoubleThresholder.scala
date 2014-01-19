package org.tindalos.principle.domain.expectations.cumulativedependency

abstract class DoubleThresholder(var threshold: Double = 0)
class ACD(private val _threshold: Double = 0) extends DoubleThresholder(_threshold) {
  def this() = this(0)
}
class NCCD(private val _threshold: Double = 0) extends DoubleThresholder(_threshold) {
  def this() = this(0)
}
class RACD(private val _threshold: Double = 0) extends DoubleThresholder(_threshold) {
  def this() = this(0)
}
