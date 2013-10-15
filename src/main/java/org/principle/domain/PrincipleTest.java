package org.principle.domain;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;

import org.junit.Test;
import org.principle.domain.detector.cycledetector.CycleDetector;
import org.principle.domain.detector.cycledetector.core.Cycle;

public class PrincipleTest {
    
    private static final String BASE_PACKAGE = "org.principle.test";
    
    private CycleDetector cycleDetector = new CycleDetector(BASE_PACKAGE);

    
    @SuppressWarnings("unchecked")
    @Test
    public void dddLayersAreIntact() throws IOException {
        
        JDepend jDepend = new JDepend();
        jDepend.addDirectory("./target/classes");
        
        jDepend.addPackage(BASE_PACKAGE);
        Collection<JavaPackage> packages = jDepend.analyze();
        
		List<Cycle> detectCycles = cycleDetector.analyze(packages);
		for (Cycle cycle : detectCycles) {
		    System.err.println(cycle);
            
        }
    }




}
