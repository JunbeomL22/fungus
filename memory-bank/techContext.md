# Tech Context

## Technologies Used
1. **Java**: Primary programming language
2. **JMH (Java Microbenchmark Harness)**: For benchmarking
3. **Maven**: Build automation and dependency management
4. **Git**: Version control

## Development Setup
1. **IDE**: VSCode (as indicated by environment)
2. **Build Tool**: Maven
3. **Build Scripts**: this project considers the network limited environment (like fire wall or no internet connection), so it has batch scripts to download dependencies and compile the project.
   - `mvn-compile.bat`: For compiling the project
   - `run.bat`: For running the application
   - `download-deps.bat`: For downloading dependencies
4. **Configuration Files**:
   - `pom.xml`: Maven configuration
   - `settings.xml`: Maven settings

## Technical Constraints
2. Dependency on Maven for building and running
3. JMH-specific configuration for benchmarks

## Dependencies
- JMH dependencies (as specified in pom.xml)
- Other Java libraries as needed

## Development Workflow
1. Clone repository from GitHub
2. Run `download-deps.bat` to download dependencies
3. Use `mvn-compile.bat` to compile the project
4. Run the application using `run.bat`
5. Run benchmarks using appropriate Maven goals
