package org.tindalos.guardrails.internal.app;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.tindalos.guardrails.internal.domain.constraints.Barrier;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceGroup;
import org.tindalos.guardrails.internal.domain.constraints.slices.SliceId;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;

/**
 * Implementation of AnalysisPlanValidator that validates analysis plan configuration,
 * specifically checking that barrier slice elements correspond to defined slices in slice groups.
 */
public class AnalysisPlanValidatorImpl implements AnalysisPlanValidator {

    @Override
    public ValidationResult validate(AnalysisPlan plan) {
        var thirdPartyOpt = plan.constraints().thirdParty();
        var slicesOpt = plan.constraints().slices();

        if (thirdPartyOpt.isEmpty()) {
            return ValidationResult.successful();
        }

        if (slicesOpt.isEmpty()) {
            return ValidationResult.failure("Slices must be defined when third-party restrictions are specified");
        }

        var slicesConstraint = slicesOpt.get();
        List<Barrier> barriers = thirdPartyOpt.map(thirdParty -> thirdParty.barriers()).orElse(List.of());

        for (var barrier : barriers) {
            String slicePath = barrier.slice();
            int dot = slicePath.indexOf('.');
            if (dot <= 0 || dot == slicePath.length() - 1) {
                return ValidationResult.failure("Invalid slice path format in barriers: '" + slicePath + "'. Expected format: 'groupName.sliceId'");
            }

            String groupName = slicePath.substring(0, dot);
            String sliceId = slicePath.substring(dot + 1);

            Optional<SliceGroup> groupOpt = slicesConstraint.sliceGroups().stream()
                    .filter(g -> g.name().equalsIgnoreCase(groupName))
                    .findFirst();

            if (groupOpt.isEmpty()) {
                return ValidationResult.failure("No slice group named '" + groupName + "' is defined under slices");
            }

            var group = groupOpt.get();
            boolean hasSlice = group.slices().keySet().stream()
                    .anyMatch(id -> id.value().equalsIgnoreCase(sliceId));

            if (!hasSlice) {
                return ValidationResult.failure("No slice named '" + sliceId + "' is defined in slice group '" + group.name() + "'");
            }
        }

        // For each slice group, check that the barriers defined for that group follow their defined order in the group.
        for (var group : slicesConstraint.sliceGroups()) {
            List<String> groupSliceOrder = group.slices().keySet().stream()
                    .map(SliceId::value)
                    .toList();

            List<String> barriersOfGroup = barriers.stream()
                    .filter(b -> {
                        int dot = b.slice().indexOf('.');
                        if (dot <= 0) return false;
                        return b.slice().substring(0, dot).equalsIgnoreCase(group.name());
                    })
                    .map(b -> {
                        int dot = b.slice().indexOf('.');
                        return b.slice().substring(dot + 1);
                    })
                    .toList();

            List<String> sortedBarriersOfGroup = barriersOfGroup.stream()
                    .sorted(Comparator.comparingInt(item -> {
                        for (int i = 0; i < groupSliceOrder.size(); i++) {
                            if (groupSliceOrder.get(i).equalsIgnoreCase(item)) {
                                return i;
                            }
                        }
                        return -1;
                    }))
                    .toList();

            if (!sortedBarriersOfGroup.equals(barriersOfGroup)) {
                return ValidationResult.failure("The order of slices in third-party barriers for '" + group.name() + "' should be the same as they are defined in that slice group");
            }
        }

        return ValidationResult.successful();
    }

}

