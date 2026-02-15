package org.tindalos.principle.domain.constraints;

public sealed interface ComponentDependency extends DoubleExpectation permits ACD, NCCD, RACD {
}
