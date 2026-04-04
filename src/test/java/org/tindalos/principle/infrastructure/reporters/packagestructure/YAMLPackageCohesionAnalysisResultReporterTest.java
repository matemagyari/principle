package org.tindalos.principle.infrastructure.reporters.packagestructure;

import java.io.File;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.tindalos.principle.domain.analyzers.structure.CohesionAnalysisResult;
import org.tindalos.principle.domain.analyzers.structure.GroupingResult;
import org.tindalos.principle.domain.core.Node;
import org.tindalos.principle.domain.analyzers.structure.NodeGroup;
import org.tindalos.principle.domain.analyzers.structure.SubgraphDecomposition;
import org.yaml.snakeyaml.Yaml;

public class YAMLPackageCohesionAnalysisResultReporterTest {

    private final YAMLPackageCohesionAnalysisResultReporter reporter = new YAMLPackageCohesionAnalysisResultReporter();

    private void assertValidYaml(String yaml) {
      var parsed = new Yaml().load(yaml);
      assertNotNull("YAML must parse to a non-null object", parsed);
    }

    private CohesionAnalysisResult makeResult(Map<String, NodeGroup> packageMap) {
        return new CohesionAnalysisResult(
                packageMap,
                Optional.empty(),
                new GroupingResult(Map.of(), List.of()),
                new SubgraphDecomposition(List.of())
        );
    }

    private Map.Entry<String, NodeGroup> pkg(String packageName, String... nodeIds) {
        var nodes = java.util.Arrays.stream(nodeIds)
                .map(id -> new Node(id, Set.of(), Set.of()))
                .collect(java.util.stream.Collectors.toSet());
        return new AbstractMap.SimpleEntry<>(packageName, new NodeGroup(nodes));
    }

    @Test
    public void noPackages_reportsEmptyPackageList() {
        new File("./principle_reports").mkdirs();
      var report = reporter.report(makeResult(Map.of()));

        assertValidYaml(report);

        var expected = """
                package_cohesion_result:
                  description: Package Cohesion Analysis
                  package_count: 0
                  detail_files:
                    - existing_packages_cohesion.txt
                    - code_structure_observations1.txt
                    - code_structure_observations2.txt
                  packages: []
                """.stripIndent();
        assertEquals(expected, report);
    }

    @Test
    public void singlePackage_reportsNameCohesionAndSize() {
        new File("./principle_reports").mkdirs();
      var result = makeResult(Map.ofEntries(
        pkg("org.example.domain", "org.example.domain.Foo", "org.example.domain.Bar")
      ));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                package_cohesion_result:
                  description: Package Cohesion Analysis
                  package_count: 1
                  detail_files:
                    - existing_packages_cohesion.txt
                    - code_structure_observations1.txt
                    - code_structure_observations2.txt
                  packages:
                    - name: org.example.domain
                      cohesion: 0.0
                      size: 2
                """.stripIndent();
        assertEquals(expected, report);
    }

    @Test
    public void multiplePackages_sortedAlphabeticallyByName() {
        new File("./principle_reports").mkdirs();
      var result = makeResult(Map.ofEntries(
        pkg("org.example.infrastructure", "org.example.infrastructure.Repo"),
        pkg("org.example.app", "org.example.app.Service"),
        pkg("org.example.domain", "org.example.domain.Entity")
      ));

        var report = reporter.report(result);

        assertValidYaml(report);

        var expected = """
                package_cohesion_result:
                  description: Package Cohesion Analysis
                  package_count: 3
                  detail_files:
                    - existing_packages_cohesion.txt
                    - code_structure_observations1.txt
                    - code_structure_observations2.txt
                  packages:
                    - name: org.example.app
                      cohesion: 0.0
                      size: 1
                    - name: org.example.domain
                      cohesion: 0.0
                      size: 1
                    - name: org.example.infrastructure
                      cohesion: 0.0
                      size: 1
                """.stripIndent();
        assertEquals(expected, report);
    }
}
