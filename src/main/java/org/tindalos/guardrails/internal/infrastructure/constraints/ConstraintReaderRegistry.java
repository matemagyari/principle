package org.tindalos.guardrails.internal.infrastructure.constraints;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Registry of custom key-based definition readers.
 * Allows extending {@link ConstraintsReader} with additional YAML section parsers
 * without modifying the built-in reader set.
 *
 * <p>Readers are represented as a functional interface that accepts the full YAML root
 * object and returns an optional key-value entry. This keeps the registry free of
 * API-layer type dependencies.
 */
public class ConstraintReaderRegistry {

    /**
     * A single custom definition reader: maps the YAML root object to an optional
     * named definition entry.
     */
    @FunctionalInterface
    public interface CustomDefinitionReader {
        Optional<Map.Entry<String, Object>> read(Map<String, Object> yamlObject);
    }

    private final List<CustomDefinitionReader> readers;

    private ConstraintReaderRegistry(List<CustomDefinitionReader> readers) {
        this.readers = List.copyOf(readers);
    }

    /** Returns an empty registry containing no custom readers. */
    public static ConstraintReaderRegistry empty() {
        return new ConstraintReaderRegistry(List.of());
    }

    /** Returns a new builder for constructing a registry. */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Applies all registered readers to the given YAML root object and collects the
     * results into a map keyed by the reader's declared key. Later registrations win
     * on key conflicts (deterministic last-writer-wins semantics).
     */
    public Map<String, Object> readAll(Map<String, Object> yamlObject) {
        return readers.stream()
                .map(r -> r.read(yamlObject))
                .flatMap(Optional::stream)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> replacement,
                        LinkedHashMap::new));
    }

    /** Builder for {@link ConstraintReaderRegistry}. */
    public static final class Builder {

        private final List<CustomDefinitionReader> readers = new ArrayList<>();

        private Builder() {
        }

        /** Registers an additional custom definition reader. */
        public Builder register(CustomDefinitionReader reader) {
            readers.add(reader);
            return this;
        }

        /** Builds the registry with all registered readers. */
        public ConstraintReaderRegistry build() {
            return new ConstraintReaderRegistry(readers);
        }
    }
}
