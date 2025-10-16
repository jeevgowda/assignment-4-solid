# Library Management System - SOLID Refactoring

This repository contains a refactored library management system that demonstrates the application of SOLID principles and Spring Data JPA. The project was completed as part of a team assignment.

## Team Members

1. Member 1: Core Service Refactoring & SRP Compliance
   - Extracted services following Single Responsibility Principle
   - Implemented Spring Data JPA integration
   - Created initial unit tests

2. Member 2: Patterns & Dependency Inversion
   - Implemented Strategy Pattern for late fee calculation
   - Applied Dependency Inversion with interfaces
   - Completed documentation

3. Member 3: Quality Assurance & Testing
   - Achieved >80% test coverage
   - Verified LSP compliance
   - Performed final integration testing

## Key Features

- **Service Layer Refactoring**: Split monolithic `LibraryService` into focused services
- **Strategy Pattern**: Flexible late fee calculation for different membership types
- **Dependency Inversion**: High-level modules depend on abstractions
- **Spring Data JPA**: Clean data access through repositories
- **Comprehensive Testing**: >88% test coverage

## SOLID Principles Applied

1. **Single Responsibility Principle**
   - Each service has one clear responsibility
   - Clean separation of concerns

2. **Open-Closed Principle**
   - Strategy pattern for extensible fee calculation
   - New membership types can be added without modification

3. **Liskov Substitution Principle**
   - Strategy implementations are interchangeable
   - Services follow interface contracts

4. **Interface Segregation Principle**
   - Focused service interfaces
   - No client depends on unused methods

5. **Dependency Inversion Principle**
   - High-level modules depend on abstractions
   - Easy to swap implementations

## Technologies Used

- Java 17
- Spring Boot
- Spring Data JPA
- H2 Database
- JUnit 5
- Mockito
- Gradle

## Build & Test

```bash
# Run all tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport
```

## Documentation

- **REFACTORING_SUMMARY.md**: Detailed refactoring steps and decisions
- **TESTING_SUMMARY.md**: Test coverage and LSP compliance details
- **LibraryFacade.java**: AI collaboration documentation

## AI Collaboration

The team utilized AI tools strategically:

1. **GitHub Copilot**
   - Interface definitions
   - Test case suggestions
   - Documentation formatting

2. **Anthropic Claude Sonnet 3.5**
   - Architecture review
   - SOLID principles validation
   - Code quality suggestions

AI was used as a tool to enhance productivity while ensuring all core implementation decisions and business logic were handled manually by the team.