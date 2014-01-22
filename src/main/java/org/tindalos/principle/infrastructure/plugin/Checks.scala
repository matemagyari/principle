package org.tindalos.principle.infrastructure.plugin

import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.tindalos.principle.domain.expectations.Layering
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint
import org.tindalos.principle.domain.expectations._

class Checks(
    var layering:Layering = null, 
    var packageCoupling:PackageCoupling = null, 
    var submodulesBlueprint:SubmodulesBlueprint = null) 
extends DesignQualityExpectations {
  
  def this(packageCoupling:PackageCoupling) = this(null, packageCoupling, null)
  def this(layering:Layering) = this(layering, null, null)
  def this() = this(null,null,null)

}