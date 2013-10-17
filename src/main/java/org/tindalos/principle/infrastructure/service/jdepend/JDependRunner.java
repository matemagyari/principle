package org.tindalos.principle.infrastructure.service.jdepend;

import java.io.IOException;
import java.util.Collection;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

public class JDependRunner {
    
    @SuppressWarnings("unchecked")
    public Collection<JavaPackage> getAnalyzedPackages(String basePackage) {
        
        try {
            JDepend jDepend = new JDepend();
            jDepend.addDirectory("./target/classes");
            
            jDepend.addPackage(basePackage);
            return jDepend.analyze();
            
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
