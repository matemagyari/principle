package org.tindalos.guardrails.internal.domain.analyzers.adp;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.core.DomainException;
import org.tindalos.guardrails.internal.domain.core.packages.PackageReference;

public class CycleTest {

    @Test
    public void testCycleCreationWithList() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");
        List<PackageReference> refs = Arrays.asList(ref1, ref2);

        Cycle cycle = new Cycle(refs);

        assertEquals(ref2, cycle.end());
        assertEquals(2, cycle.references().size());
    }

    @Test
    public void testCycleCreationWithThreeReferences() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");
        PackageReference ref3 = new PackageReference("org.example.c");

        Cycle cycle = new Cycle(Arrays.asList(ref1, ref2, ref3));

        assertEquals(ref3, cycle.end());
        assertEquals(3, cycle.references().size());
    }

    @Test
    public void testCycleCreationWithSingleReference_shouldThrowException() {
        PackageReference ref1 = new PackageReference("org.example.a");
        assertThrows(DomainException.class, () -> new Cycle(Arrays.asList(ref1)));
    }

    @Test
    public void testCycleToString() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");
        PackageReference ref3 = new PackageReference("org.example.c");

        Cycle cycle = new Cycle(Arrays.asList(ref1, ref2, ref3));
        String expected = "*-->org.example.a-->org.example.b-->org.example.c-->*";

        assertEquals(expected, cycle.toString());
    }

    @Test
    public void testCycleEquals_sameReferencesInSameOrder() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");
        PackageReference ref3 = new PackageReference("org.example.c");

        Cycle cycle1 = new Cycle(Arrays.asList(ref1, ref2, ref3));
        Cycle cycle2 = new Cycle(Arrays.asList(ref1, ref2, ref3));

        assertEquals(cycle1, cycle2);
    }

    @Test
    public void testCycleEquals_sameReferencesRotated() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");
        PackageReference ref3 = new PackageReference("org.example.c");

        // Cycle considers rotations as equal: a->b->c is same as b->c->a
        Cycle cycle1 = new Cycle(Arrays.asList(ref1, ref2, ref3));
        Cycle cycle2 = new Cycle(Arrays.asList(ref2, ref3, ref1));

        assertEquals(cycle1, cycle2);
    }

    @Test
    public void testCycleEquals_differentReferences() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");
        PackageReference ref3 = new PackageReference("org.example.c");
        PackageReference ref4 = new PackageReference("org.example.d");

        Cycle cycle1 = new Cycle(Arrays.asList(ref1, ref2, ref3));
        Cycle cycle2 = new Cycle(Arrays.asList(ref1, ref2, ref4));

        assertNotEquals(cycle1, cycle2);
    }

    @Test
    public void testCycleEquals_differentLength() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");
        PackageReference ref3 = new PackageReference("org.example.c");

        Cycle cycle1 = new Cycle(Arrays.asList(ref1, ref2));
        Cycle cycle2 = new Cycle(Arrays.asList(ref1, ref2, ref3));

        assertNotEquals(cycle1, cycle2);
    }

    @Test
    public void testCycleHashCode_sameLengthHasSameHashCode() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");
        PackageReference ref3 = new PackageReference("org.example.c");
        PackageReference ref4 = new PackageReference("org.example.d");

        // HashCode only depends on length
        Cycle cycle1 = new Cycle(Arrays.asList(ref1, ref2, ref3));
        Cycle cycle2 = new Cycle(Arrays.asList(ref1, ref2, ref4));

        assertEquals(cycle1.hashCode(), cycle2.hashCode());
    }

    @Test
    public void testCycleCompareTo() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");
        PackageReference ref3 = new PackageReference("org.example.c");

        Cycle cycle1 = new Cycle(Arrays.asList(ref1, ref2));
        Cycle cycle2 = new Cycle(Arrays.asList(ref2, ref3));

        // compareTo is based on toString()
        assertTrue(cycle1.compareTo(cycle2) < 0); // "a->b" comes before "b->c"
    }

    @Test
    public void testCycleCompareTo_same() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");

        Cycle cycle1 = new Cycle(Arrays.asList(ref1, ref2));
        Cycle cycle2 = new Cycle(Arrays.asList(ref1, ref2));

        assertEquals(0, cycle1.compareTo(cycle2));
    }

    @Test
    public void testCycleEnd() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");
        PackageReference ref3 = new PackageReference("org.example.c");

        Cycle cycle = new Cycle(Arrays.asList(ref1, ref2, ref3));

        // end() should return the last reference
        assertEquals(ref3, cycle.end());
    }

    @Test
    public void testCycleWithTwoReferences() {
        PackageReference ref1 = new PackageReference("org.example.parent");
        PackageReference ref2 = new PackageReference("org.example.parent.child");

        Cycle cycle = new Cycle(Arrays.asList(ref1, ref2));

        assertEquals(2, cycle.references().size());
        assertEquals(ref2, cycle.end());
        assertEquals("*-->org.example.parent-->org.example.parent.child-->*", cycle.toString());
    }

    @Test
    public void testCycleEquals_reflexive() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");

        Cycle cycle = new Cycle(Arrays.asList(ref1, ref2));

        assertEquals(cycle, cycle);
    }

    @Test
    public void testCycleEquals_symmetric() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");

        Cycle cycle1 = new Cycle(Arrays.asList(ref1, ref2));
        Cycle cycle2 = new Cycle(Arrays.asList(ref1, ref2));

        assertEquals(cycle1, cycle2);
        assertEquals(cycle2, cycle1);
    }

    @Test
    public void testCycleEquals_transitive() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");

        Cycle cycle1 = new Cycle(Arrays.asList(ref1, ref2));
        Cycle cycle2 = new Cycle(Arrays.asList(ref1, ref2));
        Cycle cycle3 = new Cycle(Arrays.asList(ref1, ref2));

        assertEquals(cycle1, cycle2);
        assertEquals(cycle2, cycle3);
        assertEquals(cycle1, cycle3);
    }

    @Test
    public void testCycleEquals_withNull() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");

        Cycle cycle = new Cycle(Arrays.asList(ref1, ref2));

        assertNotEquals(cycle, null);
    }

    @Test
    public void testCycleEquals_withDifferentType() {
        PackageReference ref1 = new PackageReference("org.example.a");
        PackageReference ref2 = new PackageReference("org.example.b");

        Cycle cycle = new Cycle(Arrays.asList(ref1, ref2));
        String notACycle = "not a cycle";

        assertNotEquals(cycle, notACycle);
    }
}
