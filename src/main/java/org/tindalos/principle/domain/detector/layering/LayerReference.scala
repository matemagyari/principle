package org.tindalos.principle.domain.detector.layering

case class LayerReference(private val referer:String, private val referee:String) {
  
  override def toString() = referer + " -> " + referee

}