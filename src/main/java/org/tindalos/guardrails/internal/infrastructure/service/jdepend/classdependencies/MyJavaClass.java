package org.tindalos.guardrails.internal.infrastructure.service.jdepend.classdependencies;

import java.util.Set;

import com.google.common.collect.Sets;

import jdepend.framework.JavaClass;

class MyJavaClass extends JavaClass {

    private final Set<String> dependencies = Sets.newHashSet();

    MyJavaClass(String name) {
        super(name);
    }

    void addDependency(String classPath) {
        dependencies.add(classPath);
    }

    Set<String> getDependencies() {
        return Sets.newHashSet(dependencies);
    }
}
