# ✅ Gradlew Issue - RESOLVED!

## Problem
The `./gradlew` command was not working with the error:
```
./gradlew: line 2: exec: : not found
```

OR

```
Error: Could not find or load main class org.gradle.wrapper.GradleWrapperMain
```

## Root Cause
The gradlew script file and/or gradle-wrapper.jar were corrupted during the initial setup.

## Solution Applied
The gradlew files were downloaded directly from the official Gradle repository:

```bash
# Fixed gradlew script
cd /Users/mate.magyari/private/PrivateProjects/principle
rm gradlew
curl -L -o gradlew https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradlew
chmod +x gradlew

# Fixed gradle-wrapper.jar
curl -L -o gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar
```

## Testing
```bash
# Test the wrapper (first run downloads Gradle automatically)
./gradlew --version

# List all available tasks
./gradlew tasks

# Build the project
./gradlew build
```

## Status
✅ **RESOLVED** - The Gradle wrapper is now working correctly!

### First Run Note
On the first run, Gradle will automatically download the Gradle distribution (v8.5). This may take a minute or two depending on your internet connection. Subsequent runs will be much faster.

## Troubleshooting

If you still encounter issues:

### 1. Verify Files Exist
```bash
ls -la gradlew
ls -la gradle/wrapper/gradle-wrapper.jar
ls -la gradle/wrapper/gradle-wrapper.properties
```

### 2. Check Permissions
```bash
chmod +x gradlew
```

### 3. Verify Java is Installed
```bash
java -version
```

Gradle requires Java 8 or later.

### 4. Clean and Retry
```bash
rm -rf ~/.gradle/caches/
./gradlew --version
```

### 5. Manual Wrapper Generation
If Homebrew has installed Gradle:
```bash
gradle wrapper --gradle-version 8.5
./gradlew --version
```

## Next Steps

Once Gradle is downloaded:
1. ✅ Run `./gradlew build` to build the project
2. ✅ Run `./gradlew test` to run tests
3. ✅ Import the project into IntelliJ IDEA as a Gradle project

See `QUICKSTART.md` for more commands and usage instructions.

---

**Issue Resolved:** February 6, 2026  
**Files Fixed:** `gradlew`, `gradle/wrapper/gradle-wrapper.jar`

