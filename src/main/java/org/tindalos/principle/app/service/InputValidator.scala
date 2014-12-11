package org.tindalos.principle.app.service

import org.tindalos.principle.domain.core.AnalysisInput

/**
 * Created by mate.magyari on 03/08/2014.
 */
object InputValidator {

  def validate(configuration: AnalysisInput) = {

    if (configuration.expectations.thirdParty == null) (true, "")
    else {
      val layers = configuration.expectations.layering.layers
      val barriers = configuration.expectations.thirdParty.barriers

      val invalidBarriers = for (barrier <- barriers
                                 if (!layers.contains(barrier.layer))) yield barrier



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
