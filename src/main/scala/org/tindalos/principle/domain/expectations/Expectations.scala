package org.tindalos.principle.domain.expectations

//todo make nulls Options
case class Checks(
    layering: Layering = null,
    thirdParty: Option[ThirdParty] = None,
    packageCoupling: Option[PackageCoupling] = None,
    submodulesBlueprint: Option[SubmodulesBlueprint] = None)
