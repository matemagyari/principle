package org.tindalos.principle.domain.detector.layering

case class LayerReference(referrer:String, referee:String) {
  
  override def toString() = referrer + " -> " + referee

}