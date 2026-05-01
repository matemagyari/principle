package org.tindalos.principle.internal.domain.constraints;

public sealed interface ComponentDependencyConstraint extends DoubleConstraint permits ACD, NCCD, RACD {
}
