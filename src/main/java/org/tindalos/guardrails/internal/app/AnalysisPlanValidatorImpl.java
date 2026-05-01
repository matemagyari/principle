package org.tindalos.guardrails.internal.app;

import java.util.Comparator;
import java.util.List;

import org.tindalos.guardrails.internal.domain.constraints.Barrier;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;

/**
 * Implementation of AnalysisPlanValidator that validates analysis plan configuration,
 * specifically checking that barrier layers are valid and properly ordered.
 */
public class AnalysisPlanValidatorImpl implements AnalysisPlanValidator {

    @Override
    public ValidationResult validate(AnalysisPlan plan) {
        var thirdPartyOpt = plan.constraints().thirdParty();
        var layeringOpt = plan.constraints().layering();

        if (thirdPartyOpt.isEmpty()) {
            return ValidationResult.successful();
        }

        if (layeringOpt.isEmpty()) {
            return ValidationResult.failure("Layering must be defined when third-party restrictions are specified");
        }

        List<String> layers = layeringOpt.map(l -> l.layers()).orElse(List.of());
        List<Barrier> barriers = thirdPartyOpt.map(thirdParty -> thirdParty.barriers()).orElse(List.of());

        // Check if all barrier layers are valid (exist in layering definition)
        List<Barrier> invalidBarriers = barriers.stream()
                .filter(b -> !layers.contains(b.layer()))
                .toList();

        if (!invalidBarriers.isEmpty()) {
            return ValidationResult.failure("Invalid layers specified under Barriers: " + invalidBarriers);
        }

        // Check if barriers are in the same order as layers
        List<String> layersOfBarriers = barriers.stream()
                .map(Barrier::layer)
                .toList();

        List<String> sortedLayersOfBarriers = layersOfBarriers.stream()
                .sorted(Comparator.comparingInt(layers::indexOf))
                .toList();

        if (!sortedLayersOfBarriers.equals(layersOfBarriers)) {
            return ValidationResult.failure("The order of layers in barriers should be the same as of under layering");
        }

        return ValidationResult.successful();
    }

}

