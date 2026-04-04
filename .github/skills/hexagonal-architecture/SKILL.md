# Hexagonal Architecture Skill

## Purpose
Use this skill when designing, reviewing, or refactoring code to enforce Ports and Adapters boundaries.

## When To Use
- Adding new use cases, services, adapters, or integrations
- Moving code between packages or layers
- Reviewing dependencies between modules
- Creating interfaces for external systems
- Verifying that business logic remains framework-agnostic

## Core Model
Hexagonal architecture has three major roles:

1. Domain (core business logic)
- Contains entities, value objects, domain services, and business rules
- No dependency on infrastructure details
- No framework-heavy code

2. Application (use case orchestration)
- Coordinates domain operations
- Defines ports (interfaces) for external concerns
- Calls domain logic through explicit use cases

3. Infrastructure (adapters)
- Implements ports declared by the application layer
- Handles file system, network, database, CLI, plugins, and external tooling

## Dependency Direction
Allowed direction:
- Infrastructure -> Application -> Domain

Not allowed:
- Domain -> Application
- Domain -> Infrastructure
- Application -> Infrastructure implementation classes

## Package-Level Guidance
Prefer package naming that makes boundaries explicit:
- org.tindalos.principle.domain.*
- org.tindalos.principle.app.*
- org.tindalos.principle.infrastructure.*

Keep cross-layer references intentional and minimal.

## Port and Adapter Rules
1. Define ports in the application layer
- Example: ReporterPort, PackageScannerPort, NodeBuilderPort

2. Implement ports in infrastructure
- Example: YAML reporter, JDepend scanner, file writer

3. Inject port implementations at composition root
- Use DI/container wiring classes for assembly only

4. Keep domain free of adapter concerns
- No direct calls to filesystem, logger frameworks, or plugin APIs from domain types

## Practical Checks Before Merge
1. Imports check
- Domain classes import only domain or JDK primitives
- Application classes import domain and application abstractions only
- Infrastructure may import all needed external APIs, but should expose behavior through ports

2. Construction check
- Composition root performs wiring
- No hidden singleton coupling inside domain/application behavior

3. Behavior placement check
- Business decisions belong to domain/application
- Technical details belong to infrastructure adapters

4. Test strategy check
- Domain tests: pure and fast
- Application tests: use fake/mock ports
- Infrastructure tests: adapter-specific integration behavior

## Refactoring Playbook
When a boundary violation is found:
1. Extract interface in application layer
2. Move implementation to infrastructure
3. Inject dependency through constructor
4. Replace direct adapter references with port usage
5. Add or update tests at appropriate layer

## Common Smells
- Domain object reading files directly
- Application service instantiating concrete adapters
- Infrastructure class containing business rule branching
- Circular dependencies between domain/app/infrastructure packages

## Done Criteria
A change is considered hexagon-compliant when:
- Dependencies follow the allowed direction
- Use case flow is explicit in application layer
- Infrastructure is replaceable via ports
- Domain remains isolated from delivery and persistence concerns
