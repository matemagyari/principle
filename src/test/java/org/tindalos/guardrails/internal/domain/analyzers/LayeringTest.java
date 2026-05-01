package org.tindalos.guardrails.internal.domain.analyzers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.layering.LayerReference;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.Layering;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;
import org.tindalos.guardrails.internal.infrastructure.di.Guardrails;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LayeringTest {

    private final Constraints constraints = Constraints.builder().layering(new Layering(List.of("infrastructure", "app", "domain"), 0)).build();

    @BeforeEach
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple() {
        var result = run("org.tindalos.guardrailstest.layering.simple");
        var expected = Set.of(
                new LayerReference("org.tindalos.guardrailstest.layering.simple.domain", "org.tindalos.guardrailstest.layering.simple.app"),
                new LayerReference("org.tindalos.guardrailstest.layering.simple.domain", "org.tindalos.guardrailstest.layering.simple.infrastructure"),
                new LayerReference("org.tindalos.guardrailstest.layering.simple.app", "org.tindalos.guardrailstest.layering.simple.infrastructure"));
        assertEquals(expected, Set.copyOf(result));
    }

    @Test
    public void deeper() {
        var result = run("org.tindalos.guardrailstest.layering.deeper");
        var expected = Set.of(new LayerReference(
                "org.tindalos.guardrailstest.layering.deeper.domain.aaa",
                "org.tindalos.guardrailstest.layering.deeper.app.bbb.ccc"));
        assertEquals(expected, Set.copyOf(result));
    }

    private List<LayerReference> run(String basePackage) {
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = Guardrails.createAnalyser(basePackage);
        return analyzer.analyze(plan).layerViolationsResult().get().violations();
    }

}
