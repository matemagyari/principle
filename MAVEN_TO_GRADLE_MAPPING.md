# Maven to Gradle - Configuration Mapping

## Dependencies Converted

### Scala
```xml
<!-- Maven -->
<dependency>
    <groupId>org.scala-lang</groupId>
    <artifactId>scala-library</artifactId>
    <version>2.12.18</version>
</dependency>
```

```groovy
// Gradle
implementation "org.scala-lang:scala-library:2.12.18"
```

### Dependency Scopes

| Maven Scope | Gradle Configuration |
|-------------|---------------------|
| `compile` | `implementation` |
| `provided` | `compileOnly` |
| `runtime` | `runtimeOnly` |
| `test` | `testImplementation` |
| `system` | Not recommended |

## Build Configuration

### Properties
```xml
<!-- Maven -->
<properties>
    <scala-version>2.12.18</scala-version>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
</properties>
```

```groovy
// Gradle
ext {
    scalaVersion = '2.12.18'
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
```

### Plugins

| Maven Plugin | Gradle Equivalent |
|-------------|------------------|
| `maven-scala-plugin` | `scala` plugin |
| `maven-compiler-plugin` | `java` plugin |
| `maven-surefire-plugin` | `test` task (built-in) |
| `maven-jar-plugin` | `jar` task (built-in) |
| `maven-install-plugin` | `publishToMavenLocal` |
| `maven-deploy-plugin` | `publish` (with maven-publish) |
| `maven-gpg-plugin` | `signing` plugin |

## Build Lifecycle

### Maven Phases → Gradle Tasks

```
Maven                 Gradle
-----                 ------
clean              →  clean
validate           →  (automatic)
compile            →  compileJava, compileScala
test               →  test
package            →  assemble, jar
verify             →  check
install            →  publishToMavenLocal
deploy             →  publish
```

## Project Information

### Maven
```xml
<groupId>org.tindalos.principle</groupId>
<artifactId>principle</artifactId>
<version>0.38-SNAPSHOT</version>
<packaging>maven-plugin</packaging>
```

### Gradle
```groovy
group = 'org.tindalos.principle'
version = '0.38-SNAPSHOT'
// packaging determined by plugins applied
```

## Publishing

### Maven
```xml
<distributionManagement>
    <repository>
        <id>ossrh</id>
        <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
    </repository>
    <snapshotRepository>
        <id>ossrh</id>
        <url>https://oss.sonatype.org/content/repositories/snapshots</url>
    </snapshotRepository>
</distributionManagement>
```

### Gradle
```groovy
publishing {
    repositories {
        maven {
            def releasesRepoUrl = 'https://oss.sonatype.org/service/local/staging/deploy/maven2/'
            def snapshotsRepoUrl = 'https://oss.sonatype.org/content/repositories/snapshots/'
            url = version.endsWith('SNAPSHOT') ? snapshotsRepoUrl : releasesRepoUrl
            credentials {
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }
}
```

## POM Metadata

Both Maven and Gradle support the same POM metadata:
- licenses
- developers
- scm (source control management)
- organization
- description

Gradle's `maven-publish` plugin generates a POM with all this information.

## Repositories

### Maven
```xml
<repositories>
    <repository>
        <id>central</id>
        <url>https://repo1.maven.org/maven2</url>
    </repository>
</repositories>
```

### Gradle
```groovy
repositories {
    mavenCentral()
    maven { url 'https://oss.sonatype.org/content/repositories/releases/' }
}
```

## Multi-Module Projects

### Maven
```xml
<modules>
    <module>module1</module>
    <module>module2</module>
</modules>
```

### Gradle (settings.gradle)
```groovy
include 'module1', 'module2'
```

## Profiles vs. Build Variants

### Maven Profiles
```xml
<profiles>
    <profile>
        <id>gpg</id>
        <activation>
            <property>
                <name>performRelease</name>
                <value>true</value>
            </property>
        </activation>
        <!-- configuration -->
    </profile>
</profiles>
```

### Gradle (conditional configuration)
```groovy
signing {
    required { gradle.taskGraph.hasTask('publish') }
    sign publishing.publications.mavenJava
}
```

## Commands Quick Reference

| Task | Maven | Gradle |
|------|-------|--------|
| Clean | `mvn clean` | `./gradlew clean` |
| Compile | `mvn compile` | `./gradlew compileScala` |
| Test | `mvn test` | `./gradlew test` |
| Package | `mvn package` | `./gradlew assemble` |
| Verify | `mvn verify` | `./gradlew check` |
| Install | `mvn install` | `./gradlew publishToMavenLocal` |
| Deploy | `mvn deploy` | `./gradlew publish` |
| Show deps | `mvn dependency:tree` | `./gradlew dependencies` |
| Show plugins | `mvn help:describe -Dplugin=X` | `./gradlew tasks` |
| Clean install | `mvn clean install` | `./gradlew clean build publishToMavenLocal` |
| Skip tests | `mvn install -DskipTests` | `./gradlew build -x test` |
| Debug | `mvn -X` | `./gradlew --debug` |
| Offline | `mvn -o` | `./gradlew --offline` |

## Configuration Files

| Maven | Gradle |
|-------|--------|
| `pom.xml` | `build.gradle` |
| `settings.xml` (~/.m2/) | `gradle.properties` (~/.gradle/) |
| `.mvn/` | `gradle/` |
| N/A | `settings.gradle` |

## Key Differences

1. **Build Script Language**
   - Maven: XML (declarative)
   - Gradle: Groovy/Kotlin DSL (imperative + declarative)

2. **Dependency Resolution**
   - Maven: Nearest definition wins
   - Gradle: Newest version wins (configurable)

3. **Build Performance**
   - Maven: Linear execution
   - Gradle: Parallel + incremental + cached

4. **Flexibility**
   - Maven: Convention-based, less flexible
   - Gradle: Highly customizable, programmatic

5. **Learning Curve**
   - Maven: Easier to start, limited by XML
   - Gradle: More powerful, steeper learning curve

## Migration Benefits

✅ **Speed**: Incremental builds, build cache, parallel execution  
✅ **Flexibility**: Programmatic build logic  
✅ **Dependencies**: Better conflict resolution  
✅ **Tooling**: Modern IDE support  
✅ **Community**: Growing ecosystem  
✅ **Android**: Official build tool for Android  

## Common Gotchas

1. **Transitive Dependencies**: Gradle uses dynamic versions more aggressively
2. **Plugin Syntax**: Different from Maven
3. **Build Cache**: New concept for Maven users
4. **Daemon**: Gradle uses a daemon by default (faster, but uses memory)
5. **Wrapper**: Always use `./gradlew` instead of global `gradle` command

---

See `build.gradle` for the complete Gradle configuration.

