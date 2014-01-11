package org.tindalos.principle.domain.core

case class Metrics(afferentCoupling:Int,
					efferentCoupling:Int,
					abstractness:Float,
					instability:Float,
					distance:Float) {
  
	def isCalculated() = true
}

object UndefinedMetrics extends Metrics(0,0,0,0,0) {
  
  override def isCalculated() = false
  
}