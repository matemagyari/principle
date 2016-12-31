package org.tindalos.principle.app.service

import org.tindalos.principle.domain.core.AnalysisPlan

object InputValidator {

  def validate(plan: AnalysisPlan): (Boolean, String) = {

    if (plan.expectations.thirdParty == null) (true, "")
    else {
      val layers = plan.expectations.layering.layers
      val barriers = plan.expectations.thirdParty.barriers

      val invalidBarriers = barriers.filterNot { b ⇒ layers.contains(b.layer) }

      if (!invalidBarriers.isEmpty)
        (false, s"Invalid layers specified under Barriers: ${invalidBarriers}")
      else {
        def layerOrder(a: String, b: String) = layers.indexOf(a) < layers.indexOf(b)
        val layersOfBarriers = barriers.map(_.layer)
        if (!layersOfBarriers.sortWith(layerOrder).equals(layersOfBarriers))
          (false, "The order of layers in barriers should be the same as of under layering")
        else (true, "")
      }
    }
  }

}
