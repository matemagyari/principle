package org.tindalos.principle.domain.expectations

case class Layering(var layers: List[String] = List.empty, threshold: Int = 0) extends Thresholder