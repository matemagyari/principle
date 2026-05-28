### On the importance of constraints
As a code base grows, the level of quality gets gradually harder to uphold as the ever increasing complexity outgrows the developers' capability to keep up with it. Static code analysers are meant to ease the burden on the developers and highlight the problems with the code. But even the best tools are useless if the developers can ignore them. An analyser built into the build process, so it can break it, much like CI servers do if tests fail or coverage drops, is an unignorable way to enforce good practices and keep the level of quality constantly high from the start.

# Introduction
Guardrails is an opinionated (biased towards DDD and Hexagonal Architecture-style), lightweight, non-intrusive static code analyzer written in Java (Java 21) for Java/Scala projects in the form of a Maven plugin. It runs the analysis during Maven's *compile* phase, logs the results and even breaks the build if the predefined allowed number of violations is exceeded, enforcing discipline on the developer and ensuring that the code quality never drops.

In Guardrails you can set up _constraints_ that can detect violations against OO principles, developer-imposed code-structuring rules, and can break the build process if those violations exceed the developer-defined thresholds. Guardrails currently supports _constraints_ to _watch out_ for the following
* Custom Package Groupings and Dependencies (Labels) — enforces custom layers (Onion or Hexagonal Architecture) or vertical slices/modules
* Acyclic Dependency Principle
* Stable Abstractions Principle
* Stable Dependencies Principle
* Boundaries of the use of third-party libraries (flexible mapping using Labels)
* Low Average Component Dependency

# Constraints

You can configure the constraints in yaml from version 0.34 and in xml before that. In general, each constraint runs and reports independently of the others, and can break the build if the user-defined violation threshold is exceeded. Otherwise simply reports the found problems in console and some in files.

## Acyclic Dependency Principle Constraint

Cyclic dependencies yields entangled code bases that are difficult to maintain and extend. You can find in-depth material about it [here](http://stan4j.com/advanced/acyclic-dependencies-principle.html) or [here](http://www.objectmentor.com/resources/articles/granularity.pdf). JDepend, the well-known tool Guardrails is largely based on, can detect some limited forms of cycles. It can only detect direct dependency cycles (not transitive ones), and can't detect cycles between larger blocks. Let me try to explain. Let's assume the following dependency chain

```
org.sampleapp.app.client.GameListProvider ---> org.sampleapp.domain.game.GameRepository ---> org.sampleapp.domain.core.Event ---> org.sampleapp.app.impl.SomeAssembler
```

There is no direct nor transitive dependency cycle between the packages on the end level here

```
org.sampleapp.app.client
org.sampleapp.domain.game
org.sampleapp.domain.core
org.sampleapp.app.impl
```

But on a coarser-grained level there actually is between 
```
org.sampleapp.app <----> org.sampleapp.domain
```
A UML-like figure would be nice to visualize it, but I don't know how to use one inside the wiki (yeah, shame on me). If you draw a package-diagram on a paper, you'll see what I mean.

## Labels Constraint (Grouping & Dependencies)

To organize growing codebases, Guardrails provides a unified, generic **Labels** constraint. Instead of hardcoded layering or submodule configurations, you can define custom package groupings (called *labels*) and specify the explicit dependencies allowed between them.

A `labels` constraint contains one or more named label groups (e.g., `layers` or `modules`). For each group, you define:
1. `labels`: Mapping of group names (labels) to package suffix/wildcard patterns.
2. `dependencies`: Allowed outgoing dependencies from each label to other labels.
3. `violation_threshold`: Maximum allowed illegal dependencies before the build breaks.

This flexible model unifies and replaces the previous standalone "Onion Layering" and "Modularity" constraints.

### Onion Layering Example with Labels

Most codebases use some level of concentric layering. For example in DDD there are 3 basic layers: Infrastructure, Application, and Domain. The dependencies can only point inwards:

```
      +-------------------------------------------+
      |              infrastructure               |
      |      +-----------------------------+      |
      |      |             app             |      |
      |      |      +---------------+      |      |
      |      |      |    domain     |      |      |
      |      |      +---------------+      |      |
      |      +-----------------------------+      |
      +-------------------------------------------+
```

An outer layer can depend on inner layers, but inner layers must never depend on outer layers. For example:
- `infrastructure` -> can depend on `app` and `domain`.
- `app` -> can depend on `domain`.
- `domain` -> cannot depend on anything outside itself.

About the benefits of this architectural style over the traditional layering you can read for example [here](http://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html). Guardrails can force this style of layering, detecting deviations from it. In case deviations are found, you'll see something like this in the console:

```
Labels violations (1 of allowed 0) for group 'layers':
=========================================================
Invalid dependency: domain ---> infrastructure
```

## Stable Abstractions Principle Constraint

Read about this [here](https://drive.google.com/file/d/0BwhCYaYDn8EgZjI3OTU4ZTAtYmM4Mi00MWMyLTgxN2YtMzk5YTY1NTViNTBh/view). With this Constraint you must define a _maximal allowed distance_. Each package with a higher distance will be regarded as a violation. The error report lists all these packages. For example if the _Distance_ threshold is 0.5 and _Distance_ value of the packages _org.amazon.customer_ and _org.amazon.core_are higher, then the report will list them

```
Stable Abstractions Principle violations (2 of allowed 5)
=========================================================
org.amazon.customer[0.6666667]
org.amazon.core[0.75]
```

## Stable Dependencies Principle Constraint

Read about this on the [same link](https://drive.google.com/file/d/0BwhCYaYDn8EgZjI3OTU4ZTAtYmM4Mi00MWMyLTgxN2YtMzk5YTY1NTViNTBh/view) as the SAP-one. The error report lists all the dependencies, where a package depends on an other package of higher instability (the number in angular brackets), like

```
Stable Abstractions Principle violations (1 of allowed 2)
=========================================================
org.amazon.customer[0.6666667] --> org.amazon.core[0.75] 
```

## Average Component Dependency Constraint

ACD is a numeric value telling you that picking up an arbitrary package, how many packages in average it depends on. And symmetrically how many packages depend on it. In other words, if you do a change in a package, how many of the other packages will be affected in average. Obviously we want to keep it as low as possible, so changes would affect only small part of the code instead of rippling through the whole code base. For more details read [this](https://qconsf.com/sf2009/dl/qcon-sanfran-2008/slides/AlexanderVonZitzewitz_Successful_projects_with_architecture_management.pdf). Guardrails can measure absolute ACD and relative ACD (rACD), which is the percentage-based version of ACD. E.g. 15% means an average package depends on the 15% of all packages in the code base.
```
Component Dependency Metrics
====================================
Average Component Dependency 8.92
Relative Average Component Dependency 35.23% ( of the allowed 20%)
```
## Modularity & Vertical Slices (with Labels)

Vertical slices are similar to layering, but instead of being a horizontal (concentric) partitioning, they represent a vertical grouping (cutting through layers). If you configure modules, they should be quite independent of each other, meaning that dependencies between them should be few and far between, if any (with exceptions like a shared `CORE` library upon which others depend).

In Guardrails, modularity is seamlessly configured as a named label group under the `labels` constraint. Here is how we define vertical slices, mapping packages to high-level modules and specifying exactly which modules are allowed to depend on which:

```yaml
  labels:
    - name: modules
      violation_threshold: 0
      labels:
        CORE: [domain.core]
        CONSTRAINTS: [domain.constraints]
        ANALYZERS: [domain.analyzers]
        REPORTERS: [app.reporters]
      dependencies:
        CORE: []
        CONSTRAINTS: [CORE]
        ANALYZERS: [CORE, CONSTRAINTS]
        REPORTERS: [CORE]
```

This ensures that:
- `CORE` has no outgoing dependencies.
- `CONSTRAINTS` depends only on `CORE`.
- `REPORTERS` depends only on `CORE`.
- `ANALYZERS` depends on both `CORE` and `CONSTRAINTS`.

If an unacceptable dependency is introduced, Guardrails flags it immediately. For example, if `REPORTERS` attempts to reference `CONSTRAINTS`, they will trigger a violation:

```
Labels violations (1 of allowed 0) for group 'modules':
=========================================================
Invalid dependency: REPORTERS ---> CONSTRAINTS
```

## Third-party Constraint

This constraint enables the developer to restrict access to third-party libraries to designated parts of the code. Instead of hardcoding layer packages or matching raw package prefixes, the Third-party constraint references the custom mapped package sets (defined as labels) using the format `<label_group_name>.<label_name>`.

For example, in a codebase with a `layers` label group (containing `infrastructure` and `domain` labels), we can define constraints such that:
- `layers.infrastructure` packages are allowed access to heavy external frameworks (like `org.json`, `org.yaml`, `jdepend`, etc.).
- `layers.domain` packages (the core business domain) is kept clean of heavy external framework frameworks, and is only permitted to access a few general utilities (like `org.apache.commons`).

```yaml
  third_party_restrictions:
    allowed_libraries:
      - layers.infrastructure: [org.apache.maven, org.json, org.yaml, com.google.common.collect, jdepend]
      - layers.domain: [org.apache.commons]
    violation_threshold: 0
```

Any usage of an unconfigured library in these packages will trigger a violation.

# How to use the plugin

## From version 0.34

Put the following xml-snippet into the plugins section of your pom.xml

```xml

<plugin>
    <groupId>org.tindalos.guardrails</groupId>
    <artifactid>guardrails</artifactid>
    <version>0.37</version>
    <configuration>
        <!-- Location of the configuration file relative to the project's root folder-->
        <location>guardrails.yml</location>
    </configuration>
    <executions>
        <execution>
            <!-- specify here after which lifecycle-phase the plugin should be executed -->
            <phase>compile</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

An example yaml file (referred as guardrails.yml above). Each entry under `constraints` is optional, also is any entry under `package_coupling`.

```yaml
#The root package for the analysis. All packages below are relative to this.
root_package: org.tindalos.guardrails

constraints:

  # Group packages into labels and define allowed dependency structures
  labels:
    - name: layers
      violation_threshold: 0
      labels:
        infrastructure: [infrastructure]
        app: [app]
        domain: [domain]
      dependencies:
        infrastructure: [app, domain]
        app: [domain]
        domain: []

    - name: modules
      violation_threshold: 0
      labels:
        CORE: [domain.core]
        CONSTRAINTS: [domain.constraints]
        ANALYZERS: [domain.analyzers]
        REPORTERS: [app.reporters]
      dependencies:
        CORE: []
        CONSTRAINTS: [CORE]
        ANALYZERS: [CORE, CONSTRAINTS]
        REPORTERS: [CORE]

  # third party restrictions mapped to the custom labels defined above
  third_party_restrictions:
    allowed_libraries:
      - layers.infrastructure: [org.apache.maven, org.json, org.yaml, com.google.common.collect, jdepend]
      - layers.domain: [org.apache.commons]
    violation_threshold: 0

  package_coupling:
    # number of allowed cyclic dependencies
    cyclic_dependencies_threshold: 0
    # Relative Average Component Dependency. The build will break if any package depends on more than 35% of all packages
    acd_threshold: 0.35
    # Runs cohesion analysis on the codebase and prints results under guardrails_reports
    structure_analysis_enabled: true
```

## Up to Version 0.30

Simply put the following xml-snippet into the plugins section of your pom.xml. Keep in mind that you only need to define constraints that you actually want to use. Constraints are defined under the 'check' section. Similar ones (SDP, SAP, ADP, ACD) are grouped.

```xml

<plugin>
    <groupId>org.tindalos.guardrails</groupId>
    <artifactid>guardrails</artifactid>
    <version>0.30</version>
    <configuration>
        <!-- This should the root package of you project -->
        <basePackage>com.your.root</basePackage>
        <constraints>
            <!-- The package names (relative to the baseBackage). Only downward dependencies are allowed. -->
            <layering>
                <layers>
                    <param>infrastructure</param>
                    <param>app</param>
                    <param>domain</param>
                </layers>
                <!-- The build will break if the number of layering violations exceeds 2. -->
                <violationsThreshold>2</violationsThreshold>
            </layering>
            <thirdParty>
                <barriers>
                    <barrier>
                        <layer>infrastructure</layer>
                        <components>org.apache.camel,com.mongodb</components>
                    </barrier>
                    <barrier>
                        <layer>app</layer>
                        <components>org.quartz</components>
                    </barrier>
                    <barrier>
                        <layer>domain</layer>
                        <components>com.google.common,org.joda.time,org.slf4j</components>
                    </barrier>
                </barriers>
                <violationsThreshold>0</violationsThreshold>
            </thirdParty>
            <!-- Some Constraints are grouped under 'packageCouplingConstraints'-->
            <packageCouplingConstraints>
                <!-- Acyclic Dependency Principle.The build will break if the number of cycles detected exceeds 4. -->
                <adp>
                    <violationsThreshold>4</violationsThreshold>
                </adp>
                <!-- Stable Dependencies Principle.The build will break if the number of violations detected exceeds 5. -->
                <sdp>
                    <violationsThreshold>5</violationsThreshold>
                </sdp>
                <!-- Stable Abstractions Principle. 'maxDistance' is the tolerance margin. 
                     The build will break if the number of packages with distance
                     greater than 'maxDistance' exceeds 5. -->
                <sap>
                    <maxDistance>0.5</maxDistance>
                    <violationsThreshold>5</violationsThreshold>
                </sap>
                <!-- Relative Average Component Dependency. The build will break if the rACD > 15%. -->
                <racd>
                    <threshold>0.15</threshold>
                </racd>
            </packageCouplingConstraints>
            <!-- the vertical slices (sub-modules)-->
            <submodulesBlueprint>
                <!-- the relative path of the YAML file containing the definitions -->
                <location>src/main/resources/guardrails_blueprint.yaml</location>
                <!-- The build will break if the number of violations detected exceeds 0 -->
                <violationsThreshold>0</violationsThreshold>
            </submodulesBlueprint>
        </constraints>
    </configuration>
    <executions>
        <execution>
            <!-- specify here after which lifecycle-phase the plugin should be executed -->
            <phase>compile</phase>
            <goals>
                <goal>check</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

```yml
```



The latest stable version of Guardrails is 0.37

# Future plans

SBT plugin.

# Contact

Developer: Mate Magyari, Email: mate.magyari@gmail.com
