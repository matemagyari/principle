package org.tindalos.principle.domain.expectations.cumulativedependency

abstract class DoubleThresholder(var threshold: Double = 0)
case class ACD(_threshold: Double = 0) extends DoubleThresholder(_threshold)
case class NCCD(_threshold: Double = 0) extends DoubleThresholder(_threshold)
case class RACD(_threshold: Double = 0) extends DoubleThresholder(_threshold)
