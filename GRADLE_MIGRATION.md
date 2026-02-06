# Principle - Gradle Migration Guide

## Project Conversion

This project has been successfully converted from Maven to Gradle.

## What Changed

### Files Added
- `build.gradle` - Main Gradle build configuration (replaces `pom.xml`)
- `settings.gradle` - Gradle project settings
- `gradle.properties` - Gradle and project properties
- `gradlew` / `gradlew.bat` - Gradle wrapper scripts (platform-independent builds)
- `gradle/wrapper/` - Gradle wrapper files
- `.gitignore` - Updated for Gradle

### Original Maven Files
The original `pom.xml` is still present for reference. You can:
- Keep it for historical reference
- Delete it once you've verified the Gradle build works
- Keep it if you need Maven compatibility

## Using Gradle

### Common Commands

```bash
# Clean the project
./gradlew clean

# Compile the project
./gradlew compileScala compileJava

# Run tests
./gradlew test

# Build the project (compile + test + package)
./gradlew build

# Publish to local Maven repository
./gradlew publishToMavenLocal

# Publish to remote repository (requires credentials)
./gradlew publish

# List all available tasks
./gradlew tasks

# Run with info logging
./gradlew build --info

# Run with debug logging
./gradlew build --debug
```

### Project Structure
The Gradle build maintains the same Maven directory structure:
```
src/
  main/
    scala/        - Scala source code
    java/         - Java source code (if any)
    resources/    - Resources
  test/
    scala/        - Scala test code
    resources/    - Test resources
```

## Key Features in build.gradle

### Dependencies
All Maven dependencies have been converted to Gradle format:
- `implementation` - Compile-time dependencies
- `testImplementation` - Test-only dependencies
- `compileOnly` - Provided scope dependencies (not included in runtime)

### Scala Configuration
- Scala version: 2.12.18
- Compatible with Java 8
- Incremental compilation enabled

### Publishing
Publishing configuration is included for:
- Maven Central / Sonatype OSSRH
- Automatic SNAPSHOT vs Release repository selection
- POM metadata (licenses, developers, SCM)
- GPG signing (when credentials are provided)

### Signing
To sign artifacts for release:
1. Add to `~/.gradle/gradle.properties`:
   ```properties
   signing.keyId=YOUR_KEY_ID
   signing.password=YOUR_PASSWORD
   signing.secretKeyRingFile=/path/to/secring.gpg
   ossrhUsername=YOUR_USERNAME
   ossrhPassword=YOUR_PASSWORD
   ```

2. Run:
   ```bash
   ./gradlew publish
   ```

## IntelliJ IDEA Integration

### Opening the Project
1. **File → Open** and select the project directory
2. IntelliJ will auto-detect the Gradle project
3. Select "Import Gradle project" in the popup
4. Wait for Gradle to download dependencies and index

### Refresh Gradle Project
If you modify `build.gradle`:
- Click the Gradle refresh icon in the Gradle tool window
- Or: **View → Tool Windows → Gradle** → Click refresh (🔄)

### Build in IntelliJ
- Use IntelliJ's build system: **Build → Build Project**
- Or use Gradle: **View → Tool Windows → Gradle** → Tasks → build → build

## Advantages of Gradle

1. **Faster builds** - Incremental compilation and build caching
2. **More concise** - Less verbose than Maven XML
3. **Flexible** - Groovy DSL allows custom build logic
4. **Better dependency management** - Easier to manage transitive dependencies
5. **Modern tooling** - Better IDE integration and developer experience

## Troubleshooting

### Gradle Daemon Issues
```bash
# Stop all Gradle daemons
./gradlew --stop

# Run without daemon
./gradlew build --no-daemon
```

### Clean Build
```bash
# Remove all build artifacts and caches
./gradlew clean cleanBuildCache
rm -rf .gradle build
```

### Dependency Issues
```bash
# List all dependencies
./gradlew dependencies

# Force refresh dependencies
./gradlew build --refresh-dependencies
```

### IntelliJ Not Recognizing Changes
1. **File → Invalidate Caches...**
2. Check all options
3. Click **Invalidate and Restart**

## Migration Notes

### Updated Dependencies
The build.gradle uses the same dependency versions as the original pom.xml:
- Scala: 2.12.18 (upgraded from 2.12.1)
- Java compatibility: 1.8

### Principle Checks
The custom `principleCheck` task is defined but needs to be configured based on your specific requirements. The original Maven plugin execution has been converted to a Gradle task.

### Testing
- JUnit 4.11 is configured
- ScalaTest 2.2.4 is available for Scala tests

## Next Steps

1. ✅ Verify the build works: `./gradlew build`
2. ✅ Import into IntelliJ IDEA
3. ✅ Run tests: `./gradlew test`
4. Consider updating dependencies to newer versions
5. Configure the `principleCheck` task if needed
6. Update CI/CD pipelines to use Gradle

## Resources

- [Gradle Documentation](https://docs.gradle.org/)
- [Gradle Scala Plugin](https://docs.gradle.org/current/userguide/scala_plugin.html)
- [Gradle Publishing Guide](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [Migrating from Maven to Gradle](https://docs.gradle.org/current/userguide/migrating_from_maven.html)

