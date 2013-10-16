package org.principle.domain.checker;

import org.junit.Test;
import org.principle.app.service.DesignCheckService;
import org.principle.domain.core.DesingCheckerParameters;
import org.principle.infrastructure.di.PoorMansDIContainer;

public class DesignCheckerTest {

    @Test
    public void checkItself() {
        String basePackage = "org.principle";
        DesingCheckerParameters parameters = new DesingCheckerParameters(basePackage, "infrastructure", "app", "domain");
        
        DesignCheckService designCheckService = PoorMansDIContainer.getDesignCheckService();

        DesignCheckResults results = designCheckService.analyze(parameters);

        System.err.println(results.getErrorReport());

    }

}
