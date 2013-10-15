package org.principle.plugin;

import java.io.IOException;

import org.junit.Test;

public class DesingCheckerTest {
    
    private static final String BASE_PACKAGE = "org.principle.test";
    
    @Test
    public void designChecker() throws IOException {
        
        DesingCheckerParameters parameters = new DesingCheckerParameters(BASE_PACKAGE, BASE_PACKAGE+".b1", BASE_PACKAGE+".b2", BASE_PACKAGE+".b3");
        DesignChecker designChecker = new DesignChecker(parameters);
        designChecker.execute(parameters);
    }




}
