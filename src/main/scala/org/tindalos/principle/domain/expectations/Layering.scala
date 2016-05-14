package org.tindalos.principle.domain.expectations

import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException
import org.tindalos.principle.domain.util.ListConverter
import org.tindalos.principle.domain.util.ListConverter

case class Layering(var layers: List[String] = List.empty, threshold: Int = 0) extends Thresholder {

  def setLayers(theLayers: java.util.List[String]) = {
    if (theLayers == null || theLayers.isEmpty)
      throw new InvalidConfigurationException("Invalid layering expectation! No layers specified!")
    layers = ListConverter.convert(theLayers)
  }

}