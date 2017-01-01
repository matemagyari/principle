package org.tindalos.principle.infrastructure.plugin

import org.tindalos.principle.domain.expectations._

//todo - probably the extra constructors can be get rid of
case class Checks(
    override val layering: Layering = null,
    override val thirdParty: Option[ThirdParty] = None,
    override val packageCoupling: PackageCoupling = null,
    override val submodulesBlueprint: SubmodulesBlueprint = null) extends Expectations