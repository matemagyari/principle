package org.tindalos.principle.internal.infrastructure.reporters;

import org.yaml.snakeyaml.Yaml;

import static org.junit.Assert.assertNotNull;

/**
 * Shared YAML assertion utilities for reporter tests.
 */
class YamlAssertions {

    private YamlAssertions() {}

    static void assertValidYaml(String yaml) {
        assertNotNull("YAML output must not be null", yaml);
        Object parsed = new Yaml().load(yaml);
        assertNotNull("YAML must parse to a non-null object", parsed);
    }
}

