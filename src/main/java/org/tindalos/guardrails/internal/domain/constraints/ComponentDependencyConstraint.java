package org.tindalos.guardrails.internal.domain.constraints;

public sealed interface ComponentDependencyConstraint extends DoubleConstraint permits ACD, NCCD, RACD {
}
