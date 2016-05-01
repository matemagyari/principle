### On the importance of constraints
As a code base grows, the level of quality gets gradually harder to uphold as the ever increasing complexity outgrows the developers' capability to keep up with it. Static code analysers are meant to ease the burden on the developers and highlight the problems with the code. But even the best tools are useless if the developers can ignore them. An analyser built into the build process, so it can break it, much like CI servers do if tests fail or coverage drops, is an unignorable way to enforce good practices and keep the level of quality constantly high from the start.

# Introduction
JPrinciple is a lightweight, non-intrusive static code analyzer written in Scala for Java/Scala projects in the form of a Maven plugin. It runs the analysis during the build process, logs the results and even breaks the build if the predefined allowed number of violations is exceeded, enforcing discipline on the developer and ensuring that the code quality never drops.

In JPrinciple you can set up _guards_ that can detect violations against OO principles, developer-imposed code-structuring rules, and can break the build process if those violations exceed the developer-defined thresholds. JPrinciple currently supports _guards_ to _watch out_ for the following
* Onion Layering 
* Acyclic Dependency Principle
* Stable Abstractions Principle
* Stable Dependencies Principle
* Modularity
* Boundaries of the use of third-party libraries
* Low Average Component Dependency

# Guards

You can configure the guards in yaml from version 0.34 and in xml before that. In general, each guard runs and reports independently of the others, and can break the build if the user-defined violation threshold is exceeded. Otherwise simply reports the found problems in console and some in files.

## Acyclic Dependency Principle Guard

Cyclic dependencies yields entangled code bases that are difficult to maintain and extend. You can find in-depth material about it [here](http://stan4j.com/advanced/acyclic-dependencies-principle.html) or [here](http://www.objectmentor.com/resources/articles/granularity.pdf). JDepend, the well-known tool JPrinciple is largely based on, can detect some limited forms of cycles. It can only detect direct dependency cycles (not transitive ones), and can't detect cycles between larger blocks. Let me try to explain. Let's assume the following dependency chain

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

## Onion Layering Guard

Most code bases use some level of layering. For example in DDD there are 3 basic layers, the Infrastructure, the Application and the Domain. The code structure is like an onion, where the core is the Domain, wrapped around by the Application layer, then the Infrastructure layer. The dependencies can only point inwards, so Infrastructure can depend on Application and Domain, the Application on Domain, and Domain on none of the others. About the benefits of this architectural style over the traditional layering you can read for example [here](http://blog.8thlight.com/uncle-bob/2012/08/13/the-clean-architecture.html). JPrinciple can force this style of layering, detecting deviations from it. In case of deviations are found, you'll see something like this in the console:

```
Layering violations (1 of allowed 5)
=========================================================
-------------------------------
org.someapp.infrastructure -->
org.someapp.application-->
org.someapp.domain-->
-------------------------------
```

Package _org.someapp.infrastructure_ depends on _org.someapp.application_, which depends on _org.someapp.domain_, which depends on _org.someapp.infrastructure_ , closing the circle.

## Stable Abstractions Principle Guard

Read about this [here](http://www.objectmentor.com/resources/articles/stability.pdf). With this Guard you must define a _maximal allowed distance_. Each package with a higher distance will be regarded as a violation. The error report lists all these packages. For example if the _Distance_ threshold is 0.5 and _Distance_ value of the packages _org.amazon.customer_ and _org.amazon.core_are higher, then the report will list them

```
Stable Abstractions Principle violations (2 of allowed 5)
=========================================================
org.amazon.customer[0.6666667]
org.amazon.core[0.75]
```

## Stable Dependencies Principle Guard

Read about this on the [same link](http://www.objectmentor.com/resources/articles/stability.pdf) as the SAP-one. The error report lists all the dependencies, where a package depends on an other package of higher instability (the number in angular brackets), like

```
Stable Abstractions Principle violations (1 of allowed 2)
=========================================================
org.amazon.customer[0.6666667] --> org.amazon.core[0.75] 
```

## Average Component Dependency Guard

ACD is a numeric value telling you that picking up an arbitrary package, how many packages in average it depends on. And symmetrically how many packages depend on it. In other words, if you do a change in a package, how many of the other packages will be affected in average. Obviously we want to keep it as low as possible, so changes would affect only small part of the code instead of rippling through the whole code base. For more details read this. Principle can measure absolute ACD and relative ACD (rACD), which is the percentage-based version of ACD. E.g. 15% means an average package depends on the 15% of all packages in the code base.
```
Component Dependency Metrics
====================================
Average Component Dependency 8.92
Relative Average Component Dependency 35.23% ( of the allowed 20%)
```
## Modularity Guard

Vertical slices are similar to layering, but instead of being a horizontal (or in case of onion layering a concentric) partitioning, it is, as the name suggests, vertical (in case of onion layering cutting through the layers). For a little demonstration let's assume, a bit unrealistically, that Amazon is one monolithic application. In this case it has to have a _Customer Module_, a _Payment Module_, an _Order Module_, and several others. There probably is a _Core Module_, too, containing shared parts of the other ones. A simplified version of the architecture would look like this

```
| CUSTOMER | ORDER | PAYMENT |	
| -------------------------- |
|            CORE            |
```

Each of these modules cuts through all the layers of course, but should be quite independent of each other, meaning that dependencies between them should be few and far between, if any (the exception is the _Core upon which all the others depend). In Principle you can define your vertical slices in a YAML file, explicitly defining what packages belong to a given module and what dependencies are allowed between the modules.

```yaml
# Map modules to packages 

  module-definitions: 
  
    CORE: [domain.core]
    CUSTOMER: [domain.customer,app.customer,infrastructure.customer]
    ORDER: [domain.order,app.order,infrastructure.order]
    PAYMENT: [domain.payment,app.payment,infrastructure.payment]

# Define dependencies between modules

  module-dependencies: 

    CORE: []
    CUSTOMER: [CORE]
    ORDER: [CORE]
    PAYMENT: [CORE]
```    

Under module-definitions the modules are mapped to packages, and under module-dependencies we specified that all modules can depend on Core, but not on others. It's a very simple example, but conveys the general idea. So for example if in the code there is a dependency between classes _org.amazon.app.customer.CustomerAccountManager --> org.amazon.domain.order.Order_

then you'll see this in the report:

```
Submodule Blueprint violations (1 of allowed 0)
===============================================
Invalid dependency: CUSTOMER ---> ORDER
```

If the Payment Module doesn't actually have a dependency on Core Module (contrary to what's stated under module-dependencies), you'll see

`Missing dependency: PAYMENT---> CORE`

## Third party Guard

This guard enables the developer to constrain access to third party libraries to designated parts of the code. The developer can specify which libraries she allows access to in which layer. All the layers above the specified one can use those libraries of course, but nothing under it. The visual idea behind it is that your code is a castle with multiple circles of defending walls around it. You let foreign troops leaking into your territory only until certain walls. Different troops can have different privileges, one (org.yaml) can only set up his tent inside the outmost wall (Infrastructure), the other (org.apache.commons) is allowed to enter the inner sanctum (Domain).

# How to use the plugin

## From version 0.34

Put the following xml-snippet into the plugins section of your pom.xml

```xml
<plugin>
  <groupId>org.tindalos.principle</groupId>
  <artifactid>principle</artifactid>
  <version>0.34</version>
  <configuration>
    <!-- Location of the configuration file relative to the project's root folder-->
    <location>principle.yml</location>
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

An example yaml file. Each entry under `checks` is optional, also is any entry under `package_coupling`.

```yaml
#The root package for the analysis. All packages below are relative to this.
root_package: org.tindalos.principle

checks:

  layering:
    #Layers are the packages under root package. The allowed dependencies point from left to right. 
    #'infrastructure' can depend on 'app' and 'domain', 'app' can depend on 'domain', 'domain' should not depend on any other layer
    layers: [infrastructure, app, domain]
    #number of allowed invalid dependencies
    violation_threshold: 0

  #libraries access can be configured by layers.  
  third_party_restrictions:
    allowed_libraries:
      - layer: infrastructure
        #the values serve as prefixes. E.g any library is accessable under 'org.apache.maven' ('org.apache.maven.plugins', 'org.apache.maven.archetypes, ...')
        libraries: [org.apache.maven, org.json, org.yaml, com.google.common.collect, jdepend]
      - layer: domain
        libraries: [org.apache.commons]
    violation_threshold: 0

  package_coupling:
    #number of allowed cyclic dependencies
    cyclic_dependencies_threshold: 0
    #Relative Average Component Dependency. The build will break if any package depends on more than 35% of all the packages
    acd_threshold: 0.35

  modules:
    # Map modules to packages
    module-definitions:
      EXPECTATIONS: [domain.expectations]
      CORE: [domain.core]
      AGENTSCORE: [domain.agentscore]
      AGENTS: [domain.agents, infrastructure.reporters]
      CHECKER: [domain.checker]
    # Define dependencies between modules
    module-dependencies:
      EXPECTATIONS: []
      CORE: [EXPECTATIONS]
      AGENTSCORE: [CORE, EXPECTATIONS]
      AGENTS: [CORE,AGENTSCORE,EXPECTATIONS]
      CHECKER: [CORE, AGENTSCORE]

    #number of allowed invalid dependencies
    violation_threshold: 0

#Runs some cohesion analysis on the code base and prints the results under principle_reports
structure_analysis_enabled: true
```

## Up to Version 0.30

Simply put the following xml-snippet into the plugins section of your pom.xml. Keep in mind that you only need to define guards that you actually want to use. Guards are defined under the 'check' section. Similar ones (SDP, SAP, ADP, ACD) are grouped.

```xml
<plugin>
  <groupId>org.tindalos.principle</groupId>
  <artifactid>principle</artifactid>
  <version>0.30</version>
  <configuration>
    <!-- This should the root package of you project -->
    <basePackage>com.your.root</basePackage>
    <checks>
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
      <!-- Some Guards are grouped under 'packageCoupling'--> 
      <packageCoupling>
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
    </packageCoupling>
    <!-- the vertical slices (sub-modules)-->
    <submodulesBlueprint>
      <!-- the relative path of the YAML file containing the definitions -->
      <location>src/main/resources/principle_blueprint.yaml</location>
      <!-- The build will break if the number of violations detected exceeds 0 -->
      <violationsThreshold>0</violationsThreshold>
    </submodulesBlueprint>
  </checks>
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



The latest stable version of JPrinciple is 0.34

# Contact

Developer: Mate Magyari, Email: mate.magyari@gmail.com
