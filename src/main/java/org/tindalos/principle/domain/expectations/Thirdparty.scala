package org.tindalos.principle.domain.expectations

import org.tindalos.principle.domain.expectations.exception.InvalidConfigurationException
import org.tindalos.principle.domain.util.ListConverter

/**
 * Created by mate.magyari on 26/05/2014.
 */

class Barrier(var layer:String = "",var components:String = "") {
  def this() = this("","")

  def componentList() = if (components.isEmpty) List() else components.split(",").to[List]
}

class ThirdParty(var barriers: List[Barrier], threshold: Int = 0) extends Thresholder {

  def this() = this(List(), 0)

  def setBarriers(theBarriers: java.util.List[Barrier]) = {
    if (theBarriers == null)
      throw new InvalidConfigurationException("Invalid barriers expectation!")
    barriers = ListConverter.convert(theBarriers)
  }
}
