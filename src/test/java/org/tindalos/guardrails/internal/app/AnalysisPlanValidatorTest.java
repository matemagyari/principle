package org.tindalos.guardrails.internal.app;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.constraints.Barrier;
import org.tindalos.guardrails.internal.domain.constraints.Constraints;
import org.tindalos.guardrails.internal.domain.constraints.ThirdParty;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelDefinition;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelGroup;
import org.tindalos.guardrails.internal.domain.constraints.labels.LabelId;
import org.tindalos.guardrails.internal.domain.constraints.labels.Labels;
import org.tindalos.guardrails.internal.domain.plan.AnalysisPlan;

public class AnalysisPlanValidatorTest {

    private final String basePackage = "xx";
    private final Labels aLabels = createLabels(List.of("a", "b", "c"));
    private final AnalysisPlanValidatorImpl testObj = new AnalysisPlanValidatorImpl();

    @Test
    public void wrongOrder() {
        List<Barrier> barriers = List.of(Barrier.of("layers.a"), Barrier.of("layers.c"), Barrier.of("layers.b"));
        AnalysisPlan configuration = config(barriers);

        ValidationResult result = testObj.validate(configuration);

        assertFalse(result.success());
    }

    @Test
    public void invalidBarrier() {
        List<Barrier> barriers = List.of(Barrier.of("layers.a"), Barrier.of("layers.d"));
        AnalysisPlan configuration = config(barriers);

        ValidationResult result = testObj.validate(configuration);

        assertFalse(result.success());
    }

    @Test
    public void fullCover() {
        List<Barrier> barriers = List.of(Barrier.of("layers.a"), Barrier.of("layers.b"), Barrier.of("layers.c"));
        AnalysisPlan configuration = config(barriers);

        ValidationResult result = testObj.validate(configuration);

        assertTrue(result.success());
    }

    @Test
    public void partialCover() {
        List<Barrier> barriers = List.of(Barrier.of("layers.a"), Barrier.of("layers.c"));
        AnalysisPlan configuration = config(barriers);

        ValidationResult result = testObj.validate(configuration);

        assertTrue(result.success());
    }

    @Test
    public void noThirdParty_isValid() {
        Constraints constraints = Constraints.builder()
                .labels(aLabels)
                .build();
        AnalysisPlan plan = new AnalysisPlan(constraints, basePackage);

        ValidationResult result = testObj.validate(plan);

        assertTrue(result.success());
    }

    @Test
    public void emptyBarriers_isValid() {
        ValidationResult result = testObj.validate(config(List.of()));

        assertTrue(result.success());
    }

    @Test
    public void singleValidBarrier_isValid() {
        ValidationResult result = testObj.validate(config(List.of(Barrier.of("layers.b"))));

        assertTrue(result.success());
    }

    @Test
    public void singleInvalidBarrier_fails() {
        ValidationResult result = testObj.validate(config(List.of(Barrier.of("layers.z"))));

        assertFalse(result.success());
    }

    @Test
    public void allInvalidBarriers_fails() {
        List<Barrier> barriers = List.of(Barrier.of("layers.x"), Barrier.of("layers.y"), Barrier.of("layers.z"));

        ValidationResult result = testObj.validate(config(barriers));

        assertFalse(result.success());
    }

    @Test
    public void wrongOrder_failureMessageMentionsOrder() {
        List<Barrier> barriers = List.of(Barrier.of("layers.c"), Barrier.of("layers.a"));

        ValidationResult result = testObj.validate(config(barriers));

        assertFalse(result.success());
        assertTrue(result.message().contains("order"));
    }

    @Test
    public void invalidBarrier_failureMessageMentionsInvalidLayer() {
        List<Barrier> barriers = List.of(Barrier.of("layers.z"));

        ValidationResult result = testObj.validate(config(barriers));

        assertFalse(result.success());
        assertTrue(result.message().contains("z"));
    }

    private static Labels createLabels(List<String> layers) {
        Map<LabelId, LabelDefinition> labelsMap = new java.util.LinkedHashMap<>();
        for (String layer : layers) {
            LabelId labelId = new LabelId(layer);
            labelsMap.put(labelId, new LabelDefinition(labelId, Set.of(), Set.of()));
        }
        return new Labels(List.of(new LabelGroup("layers", labelsMap, 0)));
    }

    private AnalysisPlan config(List<Barrier> barriers) {
        ThirdParty aThirdParty = new ThirdParty(barriers, 0);
        Constraints constraints = Constraints.builder()
                .labels(aLabels)
                .thirdParty(aThirdParty)
                .build();
        return new AnalysisPlan(constraints, basePackage);
    }
}