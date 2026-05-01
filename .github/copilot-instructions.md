# GitHub Copilot Instructions for JPrinciple Project

## Project Overview
JPrinciple is an architecture analysis and enforcement tool for Java/Scala codebases. It analyzes package dependencies, detects architectural violations, and enforces design principles like ADP (Acyclic Dependency Principle), SDP (Stable Dependencies Principle), and layering constraints.

## Technology Stack
- **Languages**: Java 21, Scala 2.12
- **Build Tools**: Gradle (primary), Maven (legacy support)
- **Architecture**: Hexagonal Architecture (Ports & Adapters)
- **Key Libraries**: JDepend, JUnit, ScalaTest

## ⚠️ CRITICAL ARCHITECTURAL RULES

### 1. **NO CYCLIC DEPENDENCIES BETWEEN PACKAGES**
This is the MOST IMPORTANT rule. The entire purpose of this tool is to detect and prevent cyclic dependencies.

- **NEVER** create circular package dependencies
- Packages must form a Directed Acyclic Graph (DAG)
- If Package A depends on Package B, then Package B MUST NOT depend on Package A (directly or transitively)
- Use dependency inversion (interfaces) to break potential cycles
- Always validate your changes won't introduce cycles

### 2. **Hexagonal Architecture (Ports & Adapters)**
The codebase follows hexagonal architecture principles:

#### Core Layers:
- **Domain** (`org.tindalos.guardrails.domain.*`): Pure business logic, NO external dependencies
  - Contains entities, value objects, domain services
  - Must not depend on infrastructure or application layers
  - No framework dependencies allowed
  
- **Application** (`org.tindalos.guardrails.app.*`): Application services and use cases
  - Orchestrates domain logic
  - Defines interfaces (ports) for infrastructure
  - May depend on domain layer only
  
- **Infrastructure** (`org.tindalos.guardrails.infrastructure.*`): External integrations
  - Implements application interfaces (adapters)
  - File I/O, JDepend integration, reporting
  - May depend on domain and application layers

#### Dependency Rules:
```
Infrastructure → Application → Domain
     ↓              ↓
  (adapters)    (ports)
```

**NEVER**: Domain → Application, Domain → Infrastructure, Application → Infrastructure implementations

## Code Style Guidelines

### Java Code (Java 21)

#### Use Java Records for Immutable Data
**✅ PREFERRED:**
```java
public record ValidationResult(boolean success, String message) {
    public static ValidationResult successful() {
        return new ValidationResult(true, "");
    }
}
```

**❌ AVOID:**
```java
public class ValidationResult {
    private final boolean success;
    private final String message;
    // ... constructor, getters, equals, hashCode, toString
}
```

#### Use Modern Java Features (Java 8-21)
- **Records** (Java 16+): For immutable data structures
- **Sealed classes** (Java 17+): For restricted type hierarchies
- **Pattern matching** (Java 21): For instanceof checks and switch expressions
- **Text blocks** (Java 15+): For multi-line strings
- **var** (Java 10+): For local variables with obvious types
- **Stream API** (Java 8+): For collection processing
- **Optional** (Java 8+): Instead of null checks
- **try-with-resources** (Java 8+): For resource management

#### Use Immutable Collections
**✅ PREFERRED:**
```java
List<String> layers = List.of("app", "domain", "infrastructure");
Set<String> packages = Set.of("com.example.a", "com.example.b");
Map<String, Integer> metrics = Map.of("acd", 5, "nccd", 3);
```

**❌ AVOID:**
```java
List<String> layers = new ArrayList<>();
layers.add("app");
layers.add("domain");
```

For building collections, use collectors to immutable collections:
```java
List<String> result = stream
    .filter(...)
    .map(...)
    .toList(); // Java 16+, returns immutable list
```

#### JavaDoc Guidelines
**Only for classes, interfaces, and records** - not for simple methods

**✅ REQUIRED JavaDoc:**
```java
/**
 * Represents the result of a validation operation.
 * Provides factory methods for common validation scenarios.
 *
 * @param success true if validation passed
 * @param message error message if failed
 */
public record ValidationResult(boolean success, String message) {
    public static ValidationResult successful() {
        return new ValidationResult(true, "");
    }
}
```

**❌ NO JavaDoc needed for simple methods:**
```java
// This is fine without JavaDoc:
public String getName() {
    return name;
}

public void setActive(boolean active) {
    this.active = active;
}
```

**✅ JavaDoc ONLY for complex methods:**
```java
/**
 * Analyzes package dependencies and detects cycles using Tarjan's algorithm.
 * The algorithm performs a depth-first search and uses a stack to detect
 * strongly connected components, which represent cyclic dependencies.
 *
 * @param rootPackage the root package to analyze
 * @return list of detected cycles, empty if acyclic
 */
public List<Cycle> detectCycles(String rootPackage) {
    // complex implementation
}
```

### Scala Code (Scala 2.12)

#### Prefer Immutability
- Use **val** over **var**
- Use immutable collections (`List`, `Set`, `Map`) by default
- Use **case classes** for immutable data structures

#### Functional Style
- Prefer `map`, `filter`, `flatMap` over loops
- Use `Option` instead of null
- Add explicit return types for public methods

#### Scala-Java Interoperability
- Implement Java interfaces from Scala objects
- Use `JavaConverters` (not deprecated `JavaConversions`)
- Provide getter methods when implementing Java interfaces

## Naming Conventions
- **Classes/Objects/Records**: PascalCase (e.g., `ValidationResult`, `InputValidator2`)
- **Methods/Fields**: camelCase (e.g., `validatePlan`, `getThreshold`)
- **Constants**: UPPER_SNAKE_CASE (e.g., `MAX_VIOLATIONS`)
- **Packages**: lowercase dot-separated (e.g., `org.tindalos.guardrails.domain.core`)

## Domain-Specific Guidelines

### Architecture Metrics
When working with architecture metrics, understand what they measure:

- **ACD** (Average Component Dependency): Average number of internal dependencies
- **RACD** (Relative ACD): ACD normalized by package size (ACD / NumComponents)
- **NCCD** (Normalized Cumulative Component Dependency): Similar to RACD
- **ADP** (Acyclic Dependency Principle): No circular dependencies allowed
- **SDP** (Stable Dependencies Principle): Depend on stable packages
- **SAP** (Stable Abstractions Principle): Stable packages should be abstract

### Build System Support
- Always support **both Gradle and Maven** build directories
- Gradle: `./build/classes/{scala|java}/{main|test}/`
- Maven: `./target/{classes|test-classes}/`
- Use `BuildPathUtils` for all path operations

### Error Handling
- Wrap checked exceptions in `RuntimeException` with descriptive messages
- Include problematic paths/values in error messages
- Use `Optional` for methods that may not find a value
- Validate input parameters early (fail fast)

## Design Patterns to Use

### Factory Methods
```java
public record ValidationResult(boolean success, String message) {
    public static ValidationResult successful() {
        return new ValidationResult(true, "");
    }
    
    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message);
    }
}
```

### Builder Pattern (for complex objects)
Use when objects have many optional parameters or require validation.

### Strategy Pattern (via interfaces)
Define behavior interfaces in application layer, implement in infrastructure:
```java
// Application layer (port)
public interface InputValidator2 {
    ValidationResult validate(AnalysisPlan plan);
}

// Infrastructure layer (adapter)
public object InputValidator implements InputValidator2 {
    // implementation
}
```

## Code Smells to Avoid

### ❌ Cyclic Dependencies
The cardinal sin in this project. Always check dependency direction.

### ❌ Mutable State
```java
// BAD
public class Config {
    private List<String> items = new ArrayList<>();
    public void addItem(String item) { items.add(item); }
}

// GOOD
public record Config(List<String> items) {
    public Config {
        items = List.copyOf(items); // defensive copy
    }
}
```

### ❌ Null Values
```java
// BAD
public String findName() {
    return null;
}

// GOOD
public Optional<String> findName() {
    return Optional.empty();
}
```

### ❌ Domain Logic in Infrastructure
```java
// BAD - business logic in infrastructure
public class FileReporter {
    public void report(Result result) {
        if (result.violations() > result.threshold()) { // business logic!
            writeToFile("Failed");
        }
    }
}

// GOOD - business logic in domain
public class Result {
    public boolean expectationsFailed() {
        return violations > threshold;
    }
}
```

### ❌ God Classes
Keep classes focused on a single responsibility.

### ❌ Anemic Domain Model
Domain objects should have behavior, not just data.

## Testing Standards

### Test Structure
- Unit tests in `src/test/scala` or `src/test/java`
- Test fixtures in `src/test/scala/org/tindalos/principletest/`
- Use JUnit for Java, ScalaTest for Scala

### Test Naming
- Test methods should clearly describe what they test
- Use `@Test` annotation
- Arrange-Act-Assert pattern

### Mock External Dependencies
- Mock file system operations
- Mock JDepend when testing higher-level logic
- Test both Maven and Gradle path scenarios

## Refactoring Priorities

1. **Verify no cycles** - Run cycle detection after any structural change
2. **Extract constants** for magic strings and repeated values
3. **Add JavaDoc** for classes/interfaces and complex methods only
4. **Use Optional** instead of null checks
5. **Use Records** instead of traditional classes for data
6. **Extract methods** when logic exceeds 15 lines
7. **Use immutable collections** instead of mutable ones

## Common Patterns in This Codebase

### Thresholder Pattern
Base classes for expectations with violation thresholds:
```java
// Java abstract class
public abstract class Thresholder {
    private final int violationsThreshold;
    protected Thresholder(int violationsThreshold) {
        this.violationsThreshold = violationsThreshold;
    }
    public int getViolationsThreshold() {
        return violationsThreshold;
    }
}

// Scala case class extending Java class
case class ADP(threshold: Int = 0) extends Thresholder(threshold)
```

### Result Pattern
Analysis results that can fail expectations:
```java
public interface AnalysisResult {
    boolean expectationsFailed();
}
```

### Agent Pattern
Analysis agents that check specific principles:
```scala
trait Agent {
    def analyze(input: AnalysisInput): AnalysisResult
}
```

## When Generating New Code

1. **Check architecture boundaries** - Will this create a cycle?
2. **Use records** for data structures
3. **Use immutable collections** (`List.of()`, `Set.of()`, etc.)
4. **Follow hexagonal architecture** - respect layer boundaries
5. **Add JavaDoc** only for classes/interfaces and complex methods
6. **Use modern Java features** (records, sealed classes, pattern matching, etc.)
7. **Validate inputs** early and fail fast
8. **Include unit tests** for new functionality
9. **Use meaningful names** that express intent

## Architecture Validation

Before committing any changes, verify:

1. ✅ No cyclic dependencies between packages
2. ✅ Domain layer has no external dependencies
3. ✅ Application layer doesn't depend on infrastructure implementations
4. ✅ All data structures are immutable where possible
5. ✅ Records are used for simple data structures
6. ✅ JavaDoc present for all classes/interfaces
7. ✅ Complex methods have explanatory JavaDoc

## Project-Specific Terms

- **Agent**: Analysis module (e.g., ACDAgent, ADPAgent, SDPAgent)
- **Package**: Both Java package concept and the `Package` domain class
- **Blueprint**: Predefined module dependencies structure
- **Principle**: Architecture rule being checked (ADP, SDP, SAP, etc.)
- **Violation**: When actual code structure doesn't match expected principles
- **Cohesion**: Measure of how closely related classes in a package are
- **Coupling**: Measure of dependencies between packages
- **Thresholder**: Base class for expectations with violation limits

## Quick Reference

### ✅ DO:
- Use records for immutable data
- Use `List.of()`, `Set.of()`, `Map.of()` for immutable collections
- Follow hexagonal architecture strictly
- Prevent cyclic dependencies at all costs
- Add JavaDoc to classes/interfaces and complex methods only
- Use Optional instead of null
- Use modern Java features (8-21)

### ❌ DON'T:
- Create cyclic package dependencies
- Use mutable collections when immutable will work
- Put domain logic in infrastructure layer
- Put infrastructure concerns in domain layer
- Return null (use Optional)
- Use traditional classes when records suffice
- Add JavaDoc to trivial getters/setters
- Violate layer boundaries

---

**Remember**: This is a tool that enforces architectural principles. The code must be exemplary and follow the same principles it enforces!

