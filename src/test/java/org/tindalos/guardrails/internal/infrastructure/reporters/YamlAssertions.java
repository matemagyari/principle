package org.tindalos.guardrails.internal.infrastructure.reporters;

import org.yaml.snakeyaml.Yaml;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Shared YAML assertion utilities for reporter tests.
 */
class YamlAssertions {

    private YamlAssertions() {}

    static void assertValidYaml(String yaml) {
        assertNotNull(yaml, "YAML output must not be null");
        Object parsed = new Yaml().load(yaml);
        assertNotNull(parsed, "YAML must parse to a non-null object");
    }
}

