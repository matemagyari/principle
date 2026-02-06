# Quick Start - Gradle Build

## First Time Setup

1. **Verify Java is installed** (requires Java 8 or later):
   ```bash
   java -version
   ```

2. **Make gradlew executable** (already done):
   ```bash
   chmod +x gradlew
   ```

3. **Test Gradle wrapper**:
   ```bash
   ./gradlew --version
   ```

## Build the Project

```bash
# Clean and build everything
./gradlew clean build

# Just compile
./gradlew compileScala compileJava

# Run tests
./gradlew test

# Build without tests
./gradlew build -x test
```

## IntelliJ IDEA

### Import Project
1. Open IntelliJ IDEA
2. **File → Open**
3. Select the project directory
4. Choose "Open as Gradle Project" when prompted
5. Wait for dependencies to download

### Sync Gradle
After modifying `build.gradle`:
- Click the Gradle refresh icon (🔄) in the Gradle tool window
- Or: Right-click on `build.gradle` → **Gradle → Refresh Gradle Project**

## Common Issues

### Gradle Daemon Issues
```bash
# Stop all daemons
./gradlew --stop

# Run without daemon
./gradlew build --no-daemon
```

### Build Cache Issues
```bash
# Clean everything
./gradlew clean
rm -rf .gradle build
```

### IntelliJ Issues
1. **File → Invalidate Caches → Invalidate and Restart**
2. Re-import the Gradle project

## Comparison with Maven

| Maven | Gradle |
|-------|--------|
| `mvn clean` | `./gradlew clean` |
| `mvn compile` | `./gradlew compileScala compileJava` |
| `mvn test` | `./gradlew test` |
| `mvn package` | `./gradlew assemble` |
| `mvn install` | `./gradlew publishToMavenLocal` |
| `mvn deploy` | `./gradlew publish` |

## Files Overview

- `build.gradle` - Main build configuration (like pom.xml)
- `settings.gradle` - Project settings
- `gradle.properties` - Build properties
- `gradlew` / `gradlew.bat` - Gradle wrapper (use these instead of installed gradle)
- `gradle/wrapper/` - Wrapper files (checked into version control)

## Benefits

✅ Faster incremental builds  
✅ Better dependency resolution  
✅ More concise configuration  
✅ Modern tooling support  
✅ Build caching  
✅ Parallel execution  

---

For detailed information, see `GRADLE_MIGRATION.md`

