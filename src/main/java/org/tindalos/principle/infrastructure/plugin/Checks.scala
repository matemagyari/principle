package org.tindalos.principle.infrastructure.plugin

import org.tindalos.principle.domain.expectations.DesignQualityExpectations
import org.tindalos.principle.domain.expectations.Layering
import org.tindalos.principle.domain.expectations.SubmodulesBlueprint
import org.tindalos.principle.domain.expectations.PackageCoupling

class Checks(var layering:Layering, var packageCoupling:PackageCoupling, var submodulesBlueprint:SubmodulesBlueprint) 
extends DesignQualityExpectations {
  
  def this() = this(null,null,null)

}