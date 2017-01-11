package org.tindalos.principle.infrastructure.service.jdepend.classdependencies;

import com.google.common.collect.Sets;
import jdepend.framework.JavaClass;

import java.util.Set;

public class MyJavaClass extends JavaClass {

    private Set<String> dependencies = Sets.newHashSet();

    public MyJavaClass(String name) {
        super(name);
    }

    public void addDependency(String classPath) {
        dependencies.add(classPath);
    }

    public Set<String> getDependencies() {
        return Sets.newHashSet(dependencies);
    }
}
