# Removing Maven Files (Optional)

After you've verified that the Gradle build works correctly, you can optionally remove Maven-related files.

## ⚠️ Before You Delete

**IMPORTANT**: Only proceed after:
1. ✅ Successfully running `./gradlew build`
2. ✅ Importing the project into IntelliJ as a Gradle project
3. ✅ Running tests with `./gradlew test`
4. ✅ Verifying all functionality works

## 📁 Maven Files You Can Remove

### Core Maven Files
```bash
# Remove Maven POM
rm pom.xml

# Remove Maven wrapper (if exists)
rm -rf .mvn/
rm mvnw
rm mvnw.cmd
```

### Maven Build Artifacts
```bash
# Remove Maven target directory
rm -rf target/
```

### Maven IDE Files
```bash
# Remove Eclipse Maven files (if present)
rm .classpath
rm .project
rm -rf .settings/

# Remove Maven-specific IntelliJ files (if present)
# Note: .idea/ contains both Maven and Gradle configs
# Only remove if you're starting fresh
```

## 🔄 Git Cleanup (If Using Git)

### Remove from Version Control
```bash
# Remove pom.xml from git
git rm pom.xml

# Commit the change
git add build.gradle settings.gradle gradle.properties gradlew gradlew.bat gradle/
git commit -m "Migrate from Maven to Gradle

- Replace pom.xml with build.gradle
- Add Gradle wrapper for reproducible builds
- Update dependencies to Scala 2.12.18
- Add comprehensive Gradle documentation"
```

### Update .gitignore
The `.gitignore` has already been updated to include both Maven and Gradle patterns.
If you want to remove Maven patterns:

```bash
# Edit .gitignore and remove these Maven-specific lines:
# target/
# pom.xml.tag
# pom.xml.releaseBackup
# pom.xml.versionsBackup
# pom.xml.next
# release.properties
# dependency-reduced-pom.xml
# buildNumber.properties
# .mvn/timing.properties
# .mvn/wrapper/maven-wrapper.jar
```

## 📦 Keep for Reference

You might want to **keep these** for reference:
- ✅ `pom.xml` - For comparing dependency versions
- ✅ Maven target directory - For comparing outputs (temporarily)

## 🔄 CI/CD Updates

If you're using CI/CD, update your build scripts:

### Before (Maven)
```yaml
# .travis.yml, .github/workflows/build.yml, etc.
script:
  - mvn clean install
```

### After (Gradle)
```yaml
script:
  - ./gradlew build
```

### Popular CI/CD Services

#### GitHub Actions
```yaml
- name: Build with Gradle
  run: ./gradlew build
```

#### Travis CI
```yaml
language: java
script:
  - ./gradlew build
```

#### Jenkins
```groovy
stage('Build') {
    steps {
        sh './gradlew build'
    }
}
```

#### GitLab CI
```yaml
build:
  script:
    - ./gradlew build
```

## 📝 Documentation Updates

Update these files if they reference Maven:

### README.md
Replace Maven instructions with Gradle:
```markdown
## Building

### With Gradle
```bash
./gradlew build
```

### Running Tests
```bash
./gradlew test
```
```

### Contributing Guide
Update build instructions from `mvn` to `./gradlew`

### Wiki/Documentation
Update any build documentation

## ✅ Verification Checklist

Before removing Maven files, verify:

- [ ] `./gradlew build` succeeds
- [ ] `./gradlew test` passes all tests
- [ ] IntelliJ imports project correctly as Gradle
- [ ] All dependencies are resolved
- [ ] CI/CD updated (if applicable)
- [ ] Team members notified
- [ ] Documentation updated
- [ ] Git history preserved (if important)

## 🔄 Reverting (If Needed)

If you need to go back to Maven:

### If you haven't committed:
```bash
# Restore pom.xml from git
git checkout pom.xml

# Or from backup
cp pom.xml.backup pom.xml
```

### If you committed:
```bash
# Revert the commit
git revert <commit-hash>

# Or reset (careful!)
git reset --hard HEAD~1
```

### Best Practice
Keep pom.xml for a few weeks/months until you're 100% confident.

## 📊 What to Keep

### Always Keep
- ✅ `build.gradle` - Your new build file
- ✅ `settings.gradle` - Project settings
- ✅ `gradle.properties` - Build properties
- ✅ `gradlew` & `gradlew.bat` - Wrapper scripts
- ✅ `gradle/wrapper/` - Wrapper files
- ✅ All documentation files

### Can Remove
- ❌ `pom.xml` (after verification)
- ❌ `target/` (Maven build output)
- ❌ `.mvn/` (Maven wrapper)
- ❌ `mvnw` & `mvnw.cmd` (Maven wrapper)

### Decide Based on Your Needs
- ⚠️ `.idea/` - IntelliJ settings (can regenerate)
- ⚠️ Maven-specific CI/CD configs

## 🎯 Recommended Approach

### Week 1-2
- Keep pom.xml
- Use Gradle for development
- Update CI/CD to use Gradle
- Let team adapt

### Week 3-4
- Verify everything works
- Get team feedback
- Fix any issues

### After 1 Month
- If all is good, remove pom.xml
- Clean up Maven artifacts
- Update all documentation
- Fully commit to Gradle

## 💡 Pro Tips

1. **Keep a backup**: `cp pom.xml pom.xml.backup`
2. **Use branches**: Create a `gradle-migration` branch first
3. **Test thoroughly**: Run full test suite multiple times
4. **Team communication**: Make sure everyone is on board
5. **Document changes**: Keep migration notes

## 🆘 If Something Goes Wrong

### Gradle build fails
```bash
# Clean everything
./gradlew clean --refresh-dependencies
rm -rf .gradle build

# Rebuild
./gradlew build
```

### Need to go back to Maven
```bash
# Restore pom.xml
git checkout pom.xml

# Rebuild with Maven
mvn clean install
```

### IntelliJ issues
```bash
# Close IntelliJ
# Delete .idea folder
rm -rf .idea/

# Reopen and reimport
```

## 📞 Getting Help

- Check `GRADLE_MIGRATION.md` for detailed info
- Check `QUICKSTART.md` for commands
- [Gradle Forums](https://discuss.gradle.org/)
- [Stack Overflow](https://stackoverflow.com/questions/tagged/gradle)

---

## Summary

You don't need to remove Maven files immediately. Take your time, verify everything works, and only remove them when you're confident the Gradle build is stable.

**Recommended**: Keep `pom.xml` for at least 1 month as a reference.

