---
name: "backend-code-reviewer"
description: "Reviews backend code for architecture, quality, security, and performance issues. Invoke when user submits code, before merging PRs, or when asked for code review."
---

# Backend Code Reviewer

This skill provides comprehensive code review for backend applications, focusing on architecture, code quality, security, and performance.

## When to Use

Invoke this skill when:
- User is about to commit or push backend code
- Before merging pull requests
- User asks for code review or feedback
- Need to audit existing backend codebase
- Checking for security vulnerabilities
- Evaluating code architecture and design patterns

## Review Dimensions

### 1. Architecture Review

**Layer Separation**
- Verify proper separation of concerns (Controller → Service → Repository/Mapper)
- Check for business logic in controllers
- Ensure data access logic is isolated
- Validate dependency direction (dependencies should point inward)

**Design Patterns**
- Identify missing or misused design patterns
- Check for SOLID principles violations
- Verify proper use of dependency injection
- Evaluate service boundaries and interfaces

**Code Smells Detection**
- God Class: Classes with too many responsibilities
- Feature Envy: Classes that use methods of another class more than their own
- Primitive Obsession: Overuse of primitive types instead of objects
- Shotgun Surgery: Changes that require modifying many classes
- Data Clumps: Groups of data that appear together in fields and parameters

### 2. Code Quality

**Readability & Maintainability**
- Clear and meaningful variable/method names
- Appropriate method length (generally < 50 lines)
- Reasonable class size (generally < 500 lines)
- Proper indentation and formatting
- Adequate comments for complex logic

**Error Handling**
- Proper try-catch blocks
- Meaningful error messages
- No silent failures
- Proper exception propagation
- Resource cleanup (try-with-resources, finally blocks)

**Code Duplication**
- Identify repeated code blocks
- Suggest extraction to common methods
- Recommend utility classes for shared logic

### 3. Security Review

**Input Validation**
- Validate all user inputs
- Sanitize data before processing
- Check for SQL injection vulnerabilities
- Validate file uploads (type, size, content)

**Authentication & Authorization**
- Verify proper authentication checks
- Ensure authorization on sensitive endpoints
- Check for exposed sensitive data
- Validate session/token handling

**Data Protection**
- No hardcoded credentials or secrets
- Proper encryption for sensitive data
- Secure password handling (hashing, salting)
- HTTPS enforcement for sensitive operations

### 4. Performance Review

**Database Operations**
- Check for N+1 query problems
- Identify missing indexes
- Verify proper transaction usage
- Check for inefficient queries
- Evaluate connection pooling

**Resource Management**
- Proper connection handling (database, HTTP)
- Memory leak potential
- Unnecessary object creation
- Caching opportunities

**Algorithm Efficiency**
- Time complexity analysis
- Unnecessary loops or iterations
- Blocking operations in hot paths
- Async/parallel processing opportunities

### 5. Dependency Management

**Dependency Health**
- Check for outdated dependencies
- Identify security vulnerabilities in dependencies
- Remove unused dependencies
- Verify version compatibility

**Circular Dependencies**
- Detect circular references
- Suggest refactoring to break cycles
- Evaluate dependency injection patterns

### 6. Testing Coverage

**Test Quality**
- Verify unit test coverage
- Check for meaningful test cases
- Identify untested edge cases
- Suggest integration tests where needed

## Review Process

### Step 1: Context Analysis
- Understand the code's purpose and scope
- Identify the technology stack (Spring Boot, Node.js, etc.)
- Review related files and dependencies

### Step 2: Code Analysis
- Read and analyze the code files
- Apply review dimensions systematically
- Identify specific issues with line numbers

### Step 3: Issue Prioritization
- **Critical**: Security vulnerabilities, data loss risks
- **High**: Performance bottlenecks, architectural violations
- **Medium**: Code quality issues, maintainability concerns
- **Low**: Style improvements, minor optimizations

### Step 4: Recommendations
- Provide specific, actionable feedback
- Include code examples for improvements
- Explain the "why" behind each suggestion
- Offer trade-offs and alternatives

### Step 5: Follow-up
- Document decisions made during review
- Suggest next steps for implementation
- Recommend re-review after changes

## Output Format

```
## Backend Code Review Report

### Critical Issues
[Security vulnerabilities, critical bugs]

### High Priority Issues
[Performance issues, architectural violations]

### Medium Priority Issues
[Code quality, maintainability]

### Low Priority Issues
[Style, minor improvements]

### Positive Aspects
[Good practices found in the code]

### Recommendations
[Specific actions to take]

### Next Steps
[Follow-up items and re-review schedule]
```

## Example Review

**Input**: Spring Boot Controller with SQL injection risk

```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable String id) {
    String query = "SELECT * FROM users WHERE id = " + id;
    return jdbcTemplate.queryForObject(query, User.class);
}
```

**Review Output**:

### Critical Issues
- **SQL Injection Vulnerability** (Line 3): Direct string concatenation in SQL query allows injection attacks
- **Missing Input Validation**: No validation of `id` parameter

### Recommendations
```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable Long id) {
    if (id == null || id <= 0) {
        throw new IllegalArgumentException("Invalid user ID");
    }
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));
}
```

### Next Steps
1. Implement parameterized queries
2. Add input validation
3. Re-review after changes

## Best Practices Reference

**Spring Boot**
- Use `@RestController` for API endpoints
- Leverage `@Autowired` for dependency injection
- Use `@Transactional` for database operations
- Implement proper exception handling with `@ControllerAdvice`

**Database**
- Always use parameterized queries
- Implement proper indexing strategy
- Use connection pooling
- Handle transactions appropriately

**General Backend**
- Follow RESTful conventions
- Implement proper logging
- Use async for I/O operations
- Cache frequently accessed data
- Implement rate limiting for public APIs

## Integration with Git Hooks

This skill can be integrated with pre-commit hooks to automatically review code before commits:

```bash
#!/bin/bash
# .git/hooks/pre-commit

echo "Running backend code review..."
# Call this skill to review staged files
# Block commit if critical issues found
```

## Continuous Improvement

- Track common issues across reviews
- Update review criteria based on team feedback
- Maintain a checklist of frequent violations
- Share learnings with the development team