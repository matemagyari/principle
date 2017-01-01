package org.tindalos.principle.domain.expectations


case class Checks(
    layering: Layering = null,
     thirdParty: Option[ThirdParty] = None,
     packageCoupling: PackageCoupling = null,
     submodulesBlueprint: Option[SubmodulesBlueprint] = None)
