package org.tindalos.principle.app.service

import org.tindalos.principle.domain.core.AnalysisPlan

object InputValidator {

  def validate(plan: AnalysisPlan): ValidationResult =

    plan.expectations.thirdParty
        .map { thirdParty ⇒
          val layers = plan.expectations.layering.layers
          val barriers = thirdParty.barriers

          val invalidBarriers = barriers.filterNot { b ⇒ layers.contains(b.layer) }

          if (invalidBarriers.nonEmpty)
            ValidationResult.failure(s"Invalid layers specified under Barriers: ${invalidBarriers}")
          else {
            def layerOrder(a: String, b: String) = layers.indexOf(a) < layers.indexOf(b)
            val layersOfBarriers = barriers.map(_.layer)
            if (!layersOfBarriers.sortWith(layerOrder).equals(layersOfBarriers))
              ValidationResult.failure("The order of layers in barriers should be the same as of under layering")
            else ValidationResult.successful
          }
        }
        .getOrElse(ValidationResult.successful)
}
