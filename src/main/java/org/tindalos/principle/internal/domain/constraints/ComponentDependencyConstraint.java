package org.tindalos.principle.internal.domain.constraints;

public sealed interface ComponentDependencyConstraint extends DoubleExpectation permits ACD, NCCD, RACD {
}
