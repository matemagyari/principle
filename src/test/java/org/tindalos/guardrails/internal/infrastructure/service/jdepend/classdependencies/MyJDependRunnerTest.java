package org.tindalos.guardrails.internal.infrastructure.service.jdepend.classdependencies;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class MyJDependRunnerTest {

    @Test
    public void className() {
        assertEquals("aaa.MyClass", MyJDependRunner.className("aaa.MyClass"));
        assertEquals("aaa.MyClass", MyJDependRunner.className("aaa.MyClass$InnerClass"));
        assertEquals("aaa.MyClass", MyJDependRunner.className("aaa.MyClass$InnerClass$Again"));
    }

    @Test
    public void xxx2() {
        MyJDependRunner.createNodesOfClasses("org.tindalos.guardrails");
    }
}
