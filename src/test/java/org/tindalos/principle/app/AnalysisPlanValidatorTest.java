package org.tindalos.principle.app;

import org.junit.Test;
import org.tindalos.principle.domain.constraints.Barrier;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.Layering;
import org.tindalos.principle.domain.constraints.ThirdParty;
import org.tindalos.principle.domain.AnalysisPlan;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnalysisPlanValidatorTest {

    private final String basePackage = "xx";
    private final Layering aLayering = new Layering(List.of("a", "b", "c"), 0);
    private final AnalysisPlanValidatorImpl testObj = new AnalysisPlanValidatorImpl();

    @Test
    public void wrongOrder() {
        List<Barrier> barriers = List.of(Barrier.of("a"), Barrier.of("c"), Barrier.of("b"));
        AnalysisPlan configuration = config(barriers);

        ValidationResult result = testObj.validate(configuration);

        assertFalse(result.success());
    }

    @Test
    public void invalidBarrier() {
        List<Barrier> barriers = List.of(Barrier.of("a"), Barrier.of("d"));
        AnalysisPlan configuration = config(barriers);

        ValidationResult result = testObj.validate(configuration);

        assertFalse(result.success());
    }

    @Test
    public void fullCover() {
        List<Barrier> barriers = List.of(Barrier.of("a"), Barrier.of("b"), Barrier.of("c"));
        AnalysisPlan configuration = config(barriers);

        ValidationResult result = testObj.validate(configuration);

        assertTrue(result.success());
    }

    @Test
    public void partialCover() {
        List<Barrier> barriers = List.of(Barrier.of("a"), Barrier.of("c"));
        AnalysisPlan configuration = config(barriers);

        ValidationResult result = testObj.validate(configuration);

        assertTrue(result.success());
    }

    @Test
    public void noThirdParty_isValid() {
        Constraints expectations = Constraints.builder()
                .layering(aLayering)
                .build();
        AnalysisPlan plan = new AnalysisPlan(expectations, basePackage);

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
        ValidationResult result = testObj.validate(config(List.of(Barrier.of("b"))));

        assertTrue(result.success());
    }

    @Test
    public void singleInvalidBarrier_fails() {
        ValidationResult result = testObj.validate(config(List.of(Barrier.of("z"))));

        assertFalse(result.success());
    }

    @Test
    public void allInvalidBarriers_fails() {
        List<Barrier> barriers = List.of(Barrier.of("x"), Barrier.of("y"), Barrier.of("z"));

        ValidationResult result = testObj.validate(config(barriers));

        assertFalse(result.success());
    }

    @Test
    public void wrongOrder_failureMessageMentionsOrder() {
        List<Barrier> barriers = List.of(Barrier.of("c"), Barrier.of("a"));

        ValidationResult result = testObj.validate(config(barriers));

        assertFalse(result.success());
        assertTrue(result.message().contains("order"));
    }

    @Test
    public void invalidBarrier_failureMessageMentionsInvalidLayer() {
        List<Barrier> barriers = List.of(Barrier.of("z"));

        ValidationResult result = testObj.validate(config(barriers));

        assertFalse(result.success());
        assertTrue(result.message().contains("z"));
    }

    private AnalysisPlan config(List<Barrier> barriers) {
        ThirdParty aThirdParty = new ThirdParty(barriers, 0);
        Constraints expectations = Constraints.builder()
                .layering(aLayering)
                .thirdParty(aThirdParty)
                .build();
        return new AnalysisPlan(expectations, basePackage);
    }
}

