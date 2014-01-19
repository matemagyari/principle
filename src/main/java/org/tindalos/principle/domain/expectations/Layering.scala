package org.tindalos.principle.domain.expectations

import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException
import org.tindalos.principle.domain.core.ListConverter

class Layering(var layers: List[String], threshold: Int = 0) extends Thresholder {

  def this() = this(List(), 0)

  def setLayers(theLayers: java.util.List[String]) = {
    if (theLayers == null || theLayers.isEmpty)
      throw new InvalidConfigurationException("Invalid layering expectation! No layers specified!");
    layers = ListConverter.convert(theLayers)
  }

}