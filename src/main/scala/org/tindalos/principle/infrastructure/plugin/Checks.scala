package org.tindalos.principle.infrastructure.plugin

import org.tindalos.principle.domain.expectations._

//todo - probably the extra constructors can be get rid of
class Checks(
    var layering: Layering = null,
    var thirdParty: ThirdParty = null,
    var packageCoupling: PackageCoupling = null,
    var submodulesBlueprint: SubmodulesBlueprint = null) extends Expectations {

  def this(aPackageCoupling: PackageCoupling) = this(packageCoupling = aPackageCoupling)

  def this(aLayering: Layering) = this(layering = aLayering)

  def this(aThirdParty: ThirdParty) = this(thirdParty = aThirdParty)

  def this(aLayering: Layering, aThirdParty: ThirdParty) = this(layering = aLayering, thirdParty = aThirdParty)

  def this() = this(null, null, null)

}