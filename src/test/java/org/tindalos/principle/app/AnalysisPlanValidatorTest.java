package org.tindalos.principle.app;

import org.junit.Test;
import org.tindalos.principle.domain.constraints.Barrier;
import org.tindalos.principle.domain.constraints.Constraints;
import org.tindalos.principle.domain.constraints.Layering;
import org.tindalos.principle.domain.constraints.ThirdParty;
import org.tindalos.principle.domain.core.AnalysisPlan;

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

    private AnalysisPlan config(List<Barrier> barriers) {
        ThirdParty aThirdParty = new ThirdParty(barriers, 0);
        Constraints expectations = Constraints.builder()
                .layering(aLayering)
                .thirdParty(aThirdParty)
                .build();
        return new AnalysisPlan(expectations, basePackage);
    }
}

