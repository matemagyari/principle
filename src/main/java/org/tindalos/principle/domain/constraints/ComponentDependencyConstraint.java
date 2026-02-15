package org.tindalos.principle.domain.constraints;

public sealed interface ComponentDependencyConstraint extends DoubleExpectation permits ACD, NCCD, RACD {
}
