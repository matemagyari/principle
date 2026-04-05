package org.tindalos.principle.domain.analyzers;

import org.junit.Before;
import org.junit.Test;
import org.tindalos.principle.domain.analyzers.layering.LayerReference;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.Layering;
import org.tindalos.principle.domain.plan.AnalysisPlan;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class LayeringTest {

    private final Constraints constraints = Constraints.builder().layering(new Layering(List.of("infrastructure", "app", "domain"), 0)).build();

    @Before
    public void setup() {
        TestFixture.setLogger();
    }

    @Test
    public void simple() {
        var result = run("org.tindalos.principletest.layering.simple");
        var expected = Set.of(
                new LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.app"),
                new LayerReference("org.tindalos.principletest.layering.simple.domain", "org.tindalos.principletest.layering.simple.infrastructure"),
                new LayerReference("org.tindalos.principletest.layering.simple.app", "org.tindalos.principletest.layering.simple.infrastructure"));
        assertEquals(expected, Set.copyOf(result));
    }

    @Test
    public void deeper() {
        var result = run("org.tindalos.principletest.layering.deeper");
        var expected = Set.of(new LayerReference(
                "org.tindalos.principletest.layering.deeper.domain.aaa",
                "org.tindalos.principletest.layering.deeper.app.bbb.ccc"));
        assertEquals(expected, Set.copyOf(result));
    }

    private List<LayerReference> run(String basePackage) {
        var plan = new AnalysisPlan(constraints, basePackage);
        var analyzer = PoorMansDIContainer.buildAnalyzer(basePackage);
        return analyzer.analyze(plan).layerViolationsResult().get().violations();
    }

}
