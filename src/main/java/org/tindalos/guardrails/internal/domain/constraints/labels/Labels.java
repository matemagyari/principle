package org.tindalos.guardrails.internal.domain.constraints.labels;

import java.util.List;

import org.tindalos.guardrails.internal.domain.core.Constraint;

/**
 * Top-level constraint representing all label groups configured in Guardrails.
 */
public record Labels(List<LabelGroup> labelGroups) implements Constraint {
    public Labels {
        labelGroups = List.copyOf(labelGroups);
    }
}
