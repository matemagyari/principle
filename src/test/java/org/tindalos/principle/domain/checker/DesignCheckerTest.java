package org.tindalos.principle.domain.checker;

import org.junit.Test;
import org.tindalos.principle.app.service.DesignCheckService;
import org.tindalos.principle.domain.checker.DesignCheckResults;
import org.tindalos.principle.domain.core.DesignCheckerParameters;
import org.tindalos.principle.infrastructure.di.PoorMansDIContainer;

public class DesignCheckerTest {

    @Test
    public void checkItself() {
        String basePackage = "org.tindalos.principle";
        DesignCheckerParameters parameters = new DesignCheckerParameters(basePackage, "infrastructure", "app", "domain");
        
        DesignCheckService designCheckService = PoorMansDIContainer.getDesignCheckService();

        DesignCheckResults results = designCheckService.analyze(parameters);

        System.err.println(results.getErrorReport());

    }

}
