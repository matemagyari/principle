- rename Checks to Constraints everywhere
- eliminate calling .get on Optionals wherever possible, and instead use pattern matching to handle the None case explicitly
- create YAML reporter implementations

- possible suggestions

1. create a AggregatedAnalysisResults class. It should have an Optional field for each #sym:AnalysisResult  subclass (e.g. #sym:LayerViolationsResult ). It uses the builder pattern. It should have a method to access each optional field.

2. modify #sym:AnalysisRunnerImpl to have 

3. modify #sym:run to return AggregatedAnalysisResults


        printer.printInfo(analysisResultsReporter.summary(analysisResults));

        boolean success = analysisResults.stream().noneMatch(result -> result.constraintViolated());
        return new ValidationResult(success, success ? "" : "Expectations failed");