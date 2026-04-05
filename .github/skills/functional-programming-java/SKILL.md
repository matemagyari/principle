---
name: functional-programming-java
description: Functional programming principles for JPrinciple (Java 21)
triggers:
  - functional programming
  - pure functions
  - immutability
  - higher-order functions
  - side effects
  - referential transparency
  - map filter reduce
  - lambda
  - streams
  - avoid null
---

# Functional Programming in JPrinciple

## Core Principles (Java 21)

1. **Immutability** — domain objects and results never change; return new values instead of mutating
2. **Pure functions** — analysis functions produce deterministic results with no side effects
3. **Referential transparency** — analysis results can be cached and substituted without behavior change
4. **Functional composition** — build complex analysis by combining focused checker functions
5. **Avoid shared mutable state** — eliminate threading/caching bugs

## JPrinciple-Specific Patterns

### Domain Objects as Records

Domain entities in JPrinciple should use **records** for immutable value objects and data carriers:

```java
// ✅ Package result as an immutable record
public record Package(String name, Set<String> classes, int violations) {
    public Package {
        classes = Set.copyOf(classes); // defensive copy
    }
    
    public Package withAdditionalViolations(int count) {
        return new Package(name, classes, violations + count);
    }
}

// ✅ Analysis result combining multiple metrics
public record AnalysisResult(Package pkg, ACD acdMetric, ADP adpMetric) {
    public boolean expectationsFailed() {
        return adpMetric.hasCycles() || acdMetric.exceedsThreshold();
    }
}

// ❌ Avoid mutable class — loses immutability guarantees
public class Package {
    private String name;
    private List<String> classes;
    public void addClass(String c) { classes.add(c); } // dangerous!
}
```

### Analysis Functions as Pure Transformations

Analysis functions should be pure — same input always produces same output, no side effects:

```java
// ✅ Pure analysis function
public AnalysisResult analyzePackage(Package pkg, DependencyGraph graph) {
    // Reads: pkg, graph (parameters only)
    // Returns: new AnalysisResult
    // Side effect: none
    boolean hasCycles = detectCycles(graph);
    int violations = countViolations(pkg, graph);
    return new AnalysisResult(pkg, hasCycles, violations);
}

// ❌ Impure — modifies external state
private List<AnalysisResult> results = new ArrayList<>();
public void analyzePackage(Package pkg) {
    // ...
    results.add(new AnalysisResult(...)); // side effect!
}
```

### Immutable Result Collections

Use **unmodifiable collections** throughout the analysis pipeline:

```java
// ✅ Immutable results propagate through layers
public record AnalysisResults(
    List<ACD> acdResults,
    List<ADP> adpResults,
    List<LayeringResult> layeringResults
) {
    public AnalysisResults {
        acdResults = List.copyOf(acdResults);
        adpResults = List.copyOf(adpResults);
        layeringResults = List.copyOf(layeringResults);
    }
    
    public boolean allPassed() {
        return acdResults.stream().allMatch(ACD::passed)
            && adpResults.stream().allMatch(ADP::passed)
            && layeringResults.stream().allMatch(LayeringResult::passed);
    }
}

// ✅ Build analysis results functionally
var results = List.of(
    analyzeADP(graph),
    analyzeSDP(graph),
    analyzeACDs(graph)
);

var finalResults = results.stream()
    .filter(AnalysisResult::expectationsFailed)
    .toList(); // immutable list
```

### Streams for Analysis Operations

Replace loops with stream operations for clarity and functional composition:

```java
// Transform dependencies into packages
List<Package> packages = dependencies.stream()
    .map(dep -> new Package(dep.name(), extractClasses(dep)))
    .filter(pkg -> !pkg.classes().isEmpty())
    .toList();

// Flat-map nested cycles
List<Cycle> allCycles = packages.stream()
    .flatMap(pkg -> detectCycles(pkg).stream())
    .distinct()
    .toList();

// Reduce to metrics
double averageCoupling = packages.stream()
    .mapToDouble(Package::incomingDependencies)
    .average()
    .orElse(0.0);
```

### Optional Instead of Null

Never return `null` from analysis functions; use `Optional`:

```java
// ✅ Optional signals "may not exist"
public Optional<Cycle> findDominantCycle(Package pkg) {
    return detectCycles(pkg).stream()
        .max(Comparator.comparing(Cycle::length));
}

// Chain without null checks
findDominantCycle(package)
    .map(Cycle::nodes)
    .map(Set::size)
    .ifPresent(System.out::println);

// ❌ Avoid — null is ambiguous
public Cycle findDominantCycle(Package pkg) {
    // ... returns null if not found
}
```

### Function Composition for Validators

Pipeline domain objects through validators compositionally:

```java
public interface Validator<T> {
    Optional<ValidationError> validate(T input);
}

// Compose validators
Validator<AnalysisPlan> validators = plan ->
    validatePaths(plan)
        .or(() -> validateThresholds(plan))
        .or(() -> validateLayers(plan));

// Apply in sequence
Optional<ValidationError> error = validators.validate(myPlan);
error.ifPresent(err -> reporter.report(err));
```

### Immutable Configuration

Analysis configurations should be immutable value objects:

```java
// ✅ Immutable plan with defaults
public record AnalysisPlan(
    String rootPackage,
    int adpThreshold = 0,
    int acdThreshold = 35,
    List<String> excludePatterns = List.of()
) {
    public AnalysisPlan {
        excludePatterns = List.copyOf(excludePatterns);
    }
    
    public AnalysisPlan withAdpThreshold(int threshold) {
        return new AnalysisPlan(rootPackage, threshold, acdThreshold, excludePatterns);
    }
}
```

## Hexagonal Architecture + Functional Style

### Domain Layer (Pure)

Domain analysis logic should be entirely functional:

```java
// Domain: pure analysis function, no I/O
public record CycleAnalysis(List<Cycle> cycles, boolean hasViolations) {
    public static CycleAnalysis analyze(DependencyGraph graph) {
        var cycles = ...detectCycles(graph)...;
        return new CycleAnalysis(cycles, !cycles.isEmpty());
    }
}
```

### Application Layer (Orchestration)

Application services compose domain functions and manage transactions:

```java
// Application: orchestrate domain operations
public record AnalysisService(ConstraintsReader constraints) {
    public AnalysisResults analyzeProject(AnalysisPlan plan) {
        var graph = readDependencyGraph(plan);
        var adpAnalysis = ADPAnalyzer.analyze(graph);
        var sdpAnalysis = SDPAnalyzer.analyze(graph);
        return new AnalysisResults(
            List.of(adpAnalysis),
            List.of(sdpAnalysis)
        );
    }
}
```

### Infrastructure Layer (I/O at Edges)

Keep I/O operations on the boundaries; return immutable data to domain:

```java
// Infrastructure: read file, return immutable graph
public class FileBasedGraphReader implements GraphReader {
    public DependencyGraph read(Path path) {
        var lines = Files.readAllLines(path);
        var dependencies = lines.stream()
            .map(this::parseLine)
            .toList();
        return new DependencyGraph(List.copyOf(dependencies));
    }
}
```

## Validation Checklist for JPrinciple

- [ ] Result classes use `record` for immutable data carriers
- [ ] Collection parameters defensively copied with `List.copyOf()`, `Set.copyOf()`
- [ ] Analysis functions are pure (same input → same output, no side effects)
- [ ] Methods return `Optional` instead of `null`
- [ ] Collections built with `List.of()`, `Set.of()` or collected via `.toList()`
- [ ] Side effects isolated to infrastructure layer (file I/O, reporting)
- [ ] Domain logic only operates on immutable values
- [ ] Streams used instead of loops for clarity
- [ ] Results never mutated; new instances returned instead
- [ ] No shared mutable state between analysis stages

## Code Smells to Fix

| ❌ Avoid | ✅ Fix To |
|---|---|
| `List<Results> results = new ArrayList<>();` | `List.of(result1, result2, ...)` or `.toList()` stream |
| `public void analyze()` returning nothing | `public AnalysisResult analyze()` returns result |
| `if (result == null)` | `Optional<Result>` with `.map()` / `.ifPresent()` |
| Mutable fields in result classes | Use `record` with defensive copies |
| `for (item : items)` | `items.stream().map().filter().toList()` |
| `results.add(...) // side effect` | `return new Results(items, value)` |
| Null checks scattered throughout | `Optional` chains in domain layer |

## Example: JPrinciple Analysis Function Refactor

### Before (Imperative, Mutable)

```java
public class ADPChecker {
    private List<Violation> violations = new ArrayList<>();
    
    public void checkADP(Package pkg, DependencyGraph graph) {
        for (Package dep : pkg.getDependencies()) {
            if (hasCycle(dep, graph)) {
                violations.add(new Violation(pkg, dep));
            }
        }
    }
    
    public List<Violation> getViolations() {
        return violations;
    }
}
```

### After (Functional, Immutable)

```java
public record ADPAnalysis(List<Cycle> cycles, boolean passed) {
    public static ADPAnalysis analyze(Package pkg, DependencyGraph graph) {
        var cycles = pkg.dependencies().stream()
            .flatMap(dep -> detectCycles(dep, graph).stream())
            .distinct()
            .toList();
        
        return new ADPAnalysis(cycles, cycles.isEmpty());
    }
    
    public boolean expectationsFailed() {
        return !passed;
    }
}
```

## Further Reading

See [copilot-instructions.md](../../../copilot-instructions.md) for:
- Hexagonal Architecture constraints
- Domain layer purity requirements
- JavaDoc standards
- Immutable collection guidelines
