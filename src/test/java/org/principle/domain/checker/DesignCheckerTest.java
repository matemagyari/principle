package org.principle.domain.checker;

import org.junit.Test;
import org.principle.domain.core.DesingCheckerParameters;

public class DesignCheckerTest {

    @Test
    public void checkItself() {
        String basePackage = "org.principle";
        DesingCheckerParameters parameters = new DesingCheckerParameters(basePackage, "infrastructure", "app", "domain");
        DesignChecker designChecker = new DesignChecker(parameters);
        DesignCheckResults results = designChecker.execute();

        System.err.println(results.getErrorReport());

    }

}
