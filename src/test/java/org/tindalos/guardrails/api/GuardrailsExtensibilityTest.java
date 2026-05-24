package org.tindalos.guardrails.api;

import org.junit.jupiter.api.Test;
import org.tindalos.guardrails.internal.domain.analyzers.TestFixture;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GuardrailsExtensibilityTest {

  GuardrailsExtensibilityTest() {
    TestFixture.setLogger();
  }

    private record XResult(boolean constraintViolated, String message) implements AnalysisResult {
    }

    private interface XResultReporter extends AnalysisResultReporter<XResult> {
    }

    private static final class YAMLXResultReporter implements XResultReporter {

        @Override
        public Class<XResult> resultType() {
            return XResult.class;
        }

        @Override
        public String report(XResult result) {
            return """
                    x_result:
                      constraint_violated: %s
                      message: %s
                    """.formatted(result.constraintViolated(), result.message());
        }
    }

    private static final class XResultAnalyzer implements Analyzer<XResult> {

        @Override
      public XResult analyze(AnalysisPlan plan) {
        return plan.customDefinition("x_result", XResult.class)
            .orElse(new XResult(false, "missing"));
      }

      @Override
      public boolean isEnabled(AnalysisPlan plan) {
        return plan.customDefinition("x_result", XResult.class).isPresent();
        }
    }

    private static final class XResultReader implements ConstraintDefinitionReader<XResult> {

        @Override
        public String key() {
            return "x_result";
        }

        @Override
        @SuppressWarnings("unchecked")
        public Optional<XResult> read(Map<String, Object> yamlObject) {
            return Optional.ofNullable((Map<String, Object>) yamlObject.get("x_result"))
                    .map(section -> new XResult(
                            (Boolean) section.getOrDefault("constraint_violated", false),
                            String.valueOf(section.getOrDefault("message", "~"))));
        }
    }

    @Test
    void builder_allows_registering_custom_reader_analyzer_and_reporter() throws IOException {
        var yaml = """
                root_package: org.tindalos.guardrails.internal
                constraints:
                  layering:
                    layers: [infrastructure, app, domain]
                    violation_threshold: 0
                  third_party_restrictions:
                    allowed_libraries:
                      - layer: infrastructure
                        libraries: [org.apache.maven, org.json, org.yaml, com.google.common.collect, jdepend]
                      - layer: domain
                        libraries: [org.apache.commons]
                    violation_threshold: 0
                  package_coupling:
                    cyclic_dependencies_threshold: 0
                    acd_threshold: 0.40
                    structure_analysis_enabled: true
                  modules:
                    module-definitions:
                      CORE: [domain.core]
                      CONSTRAINTS: [domain.constraints]
                      ANALYZERS: [domain.analyzers]
                      REPORTERS: [app.reporters]
                    module-dependencies:
                      CORE: []
                      CONSTRAINTS: [CORE]
                      ANALYZERS: [CORE, CONSTRAINTS]
                      REPORTERS: [CORE, ANALYZERS]
                    violation_threshold: 0
                x_result:
                  constraint_violated: true
                  message: custom-check
                """;

        var tempFile = Files.createTempFile("guardrails-extensible-", ".yml");
        Files.writeString(tempFile, yaml, StandardCharsets.UTF_8);

        var builder = Guardrails.builder("org.tindalos.guardrails.internal")
          .register(Guardrails.extension(new XResultAnalyzer(), new YAMLXResultReporter(), new XResultReader()));

        var plan = builder.readPlan(Optional.of(tempFile.toString()));
        var analyzer = builder.build();

        var outcome = analyzer.analyze(plan);

        assertTrue(outcome.hasViolations());
        assertTrue(outcome.summaryYaml().contains("x_result:"));
        assertTrue(outcome.summaryYaml().contains("custom-check"));

        Files.deleteIfExists(Path.of(tempFile.toString()));
    }
}
