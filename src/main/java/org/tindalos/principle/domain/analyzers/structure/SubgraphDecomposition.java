package org.tindalos.principle.domain.analyzers.structure;

import java.util.List;

public record SubgraphDecomposition(List<Peninsula> peninsulas) {
    public SubgraphDecomposition {
        peninsulas = List.copyOf(peninsulas);
    }
}
