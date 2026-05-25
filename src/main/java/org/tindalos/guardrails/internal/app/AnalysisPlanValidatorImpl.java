package org.tindalos.guardrails.internal.app;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.tindalos.guardrails.internal.domain.constraints.Barrier;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelGroup;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelId;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;

/**
 * Implementation of AnalysisPlanValidator that validates analysis plan configuration,
 * specifically checking that barrier label elements correspond to defined labels in label groups.
 */
public class AnalysisPlanValidatorImpl implements AnalysisPlanValidator {

    @Override
    public ValidationResult validate(AnalysisPlan plan) {
        var thirdPartyOpt = plan.constraints().thirdParty();
        var labelsOpt = plan.constraints().labels();

        if (thirdPartyOpt.isEmpty()) {
            return ValidationResult.successful();
        }

        if (labelsOpt.isEmpty()) {
            return ValidationResult.failure("Labels must be defined when third-party restrictions are specified");
        }

        var labelsConstraint = labelsOpt.get();
        List<Barrier> barriers = thirdPartyOpt.map(thirdParty -> thirdParty.barriers()).orElse(List.of());

        for (var barrier : barriers) {
            String labelPath = barrier.label();
            int dot = labelPath.indexOf('.');
            if (dot <= 0 || dot == labelPath.length() - 1) {
                return ValidationResult.failure("Invalid label path format in barriers: '" + labelPath + "'. Expected format: 'groupName.labelId'");
            }

            String groupName = labelPath.substring(0, dot);
            String labelId = labelPath.substring(dot + 1);

            Optional<LabelGroup> groupOpt = labelsConstraint.labelGroups().stream()
                    .filter(g -> g.name().equalsIgnoreCase(groupName))
                    .findFirst();

            if (groupOpt.isEmpty()) {
                return ValidationResult.failure("No label group named '" + groupName + "' is defined under labels");
            }

            var group = groupOpt.get();
            boolean hasLabel = group.labels().keySet().stream()
                    .anyMatch(id -> id.value().equalsIgnoreCase(labelId));

            if (!hasLabel) {
                return ValidationResult.failure("No label named '" + labelId + "' is defined in label group '" + group.name() + "'");
            }
        }

        // For each label group, check that the barriers defined for that group follow their defined order in the group.
        for (var group : labelsConstraint.labelGroups()) {
            List<String> groupLabelOrder = group.labels().keySet().stream()
                    .map(LabelId::value)
                    .toList();

            List<String> barriersOfGroup = barriers.stream()
                    .filter(b -> {
                        int dot = b.label().indexOf('.');
                        if (dot <= 0) return false;
                        return b.label().substring(0, dot).equalsIgnoreCase(group.name());
                    })
                    .map(b -> {
                        int dot = b.label().indexOf('.');
                        return b.label().substring(dot + 1);
                    })
                    .toList();

            List<String> sortedBarriersOfGroup = barriersOfGroup.stream()
                    .sorted(Comparator.comparingInt(item -> {
                        for (int i = 0; i < groupLabelOrder.size(); i++) {
                            if (groupLabelOrder.get(i).equalsIgnoreCase(item)) {
                                return i;
                            }
                        }
                        return -1;
                    }))
                    .toList();

            if (!sortedBarriersOfGroup.equals(barriersOfGroup)) {
                return ValidationResult.failure("The order of labels in third-party barriers for '" + group.name() + "' should be the same as they are defined in that label group");
            }
        }

        return ValidationResult.successful();
    }

}

