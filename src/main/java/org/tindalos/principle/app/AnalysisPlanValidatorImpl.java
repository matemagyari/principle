package org.tindalos.principle.app;

import org.tindalos.principle.domain.constraints.Barrier;
import org.tindalos.principle.domain.constraints.ThirdParty;
import org.tindalos.principle.domain.core.AnalysisPlan;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of AnalysisPlanValidator that validates analysis plan configuration,
 * specifically checking that barrier layers are valid and properly ordered.
 */
public class AnalysisPlanValidatorImpl implements AnalysisPlanValidator {

    @Override
    public ValidationResult validate(AnalysisPlan plan) {
        Optional<ThirdParty> thirdPartyOpt = plan.constraints().thirdParty();

        if (thirdPartyOpt.isEmpty()) {
            return ValidationResult.successful();
        }

        ThirdParty thirdParty = thirdPartyOpt.get();
        List<String> layers = plan.constraints().layering().layers();
        List<Barrier> barriers = thirdParty.barriers();

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

