package org.tindalos.principle.domain.expectations;

public sealed interface ComponentDependency extends DoubleExpectation permits ACD, NCCD, RACD {
}
