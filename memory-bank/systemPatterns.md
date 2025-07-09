# System Patterns

## Architecture Overview
The Fungus project follows a typical Java application structure with additional benchmarking capabilities using JMH. The main components are:

1. **Main Application**: Located in `src/main/java/com/junbeom/App.java`
2. **Benchmarks**: Located in `src/jmh/java/com/junbeom/`
3. **Tests**: Located in `src/test/java/com/junbeom/AppTest.java`

## Key Technical Decisions
1. **Use of JMH for Benchmarking**: JMH is integrated to provide accurate microbenchmarking capabilities
2. **Maven for Build and Dependency Management**: Maven is used to manage project dependencies and build processes
3. **Separation of Concerns**: Benchmarks are separated from the main application code
4. **Batch Scripts for Build and Run**: Batch scripts are provided for common operations

## Design Patterns
1. **Benchmark Pattern**: Using JMH annotations to define benchmarks
2. **Maven Project Structure**: Following standard Maven directory layout
3. **Batch Script Automation**: Using batch scripts to automate common tasks

## Component Relationships
1. **Main Application**: The core functionality of the project
2. **Benchmarks**: Measure performance characteristics of the application
3. **Tests**: Verify the functionality of the main application
4. **Build Scripts**: Automate build and run processes
5. **Configuration Files**: Manage dependencies and build settings

## Data Flow
1. User runs the application using `run.bat`
2. Application executes from `src/main/java/com/junbeom/App.java`
3. Benchmarks can be run separately using Maven goals
4. Results are output to the console or log files
