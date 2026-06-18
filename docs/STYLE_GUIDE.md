# Code Quality Standards for Pizza API

## Overview

This project enforces code quality through Maven Checkstyle integration. Every build runs `mvn clean package` which validates your code against these rules.

## The 5 Laws (Code Philosophy)

Our checkstyle configuration encodes the **Code Philosophy**:

### 1. Guard Clauses
- **Rule**: Avoid deep nesting by using early returns (`if (!valid) return; doWork();`)
- **Checkstyle Enforcement**: Cyclomatic complexity ≤ 10, max nested try blocks = 2 (3 levels total)

### 2. Parse Don't Validate
- **Rule**: Data parsed at boundaries, trusted internally
- **Checkstyle Enforcement**: Fail fast with descriptive exception messages; no nested try/catch without proper handling

### 3. Atomic Predictability
- **Rule**: Pure functions where possible; same input → same output
- **Checkstyle Enforcement**: No void mutation methods (enforced by Javadoc requirements), simplify boolean returns

### 4. Fail Fast
- **Rule**: Invalid states halt immediately with descriptive errors
- **Checkstyle Enforcement**: Explicit exception handling required, no suppressed exceptions

### 5. Intentional Naming
- **Rule**: Code reads like English sentences
- **Checkstyle Enforcement**: PascalCase classes, camelCase methods/fields, UPPER_CASE constants

## Checkstyle Rules Detail

### JavaDoc Style (Required)

All public and protected API elements MUST include Javadoc:

```java
/**
 * Processes the pizza order with validation.
 *
 * @param order The pizza order to process
 * @return ProcessedOrder containing validated data
 * @throws InvalidOrderException if order is invalid
 */
public ProcessedOrder validateAndProcess(Order order) {
    // ...
}
```

**Why**: Javadoc serves as living documentation and prevents API drift.

### Naming Conventions

| Element | Format | Example |
|---------|--------|----------|
| Classes/Interfaces | PascalCase | `PizzaOrder`, `OrderValidator` |
| Methods | camelCase (get/is/has prefix) | `isReady()`, `validateOrder()` |
| Fields | camelCase | `processedOrders`, `currentBatch` |
| Parameters | camelCase | `order`, `validationResult` |
| Constants | UPPER_CASE | `MAX_SIZE`, `DEFAULT_TAX_RATE` |

### Guard Clauses (Anti-Nesting)

**Maximum 3 levels of indentation**. Use guard clauses:

```java
// BAD: Deep nesting
public void processOrder(Order order) {
    if (order != null) {  // level 1
        if (isValid(order)) {  // level 2
            if (!isCancelled()) {  // level 3
                doWork();
            }
        }
    }
}

// GOOD: Guard clauses + early returns
public void processOrder(Order order) {
    if (order == null || !isValid(order)) return;  // guard clause
    if (isCancelled()) return;  // early exit
    
doWork();
}
```

**Checkstyle**: Nested try blocks limited to depth 2. If you exceed this, refactor with:
- Extract method calls
- Guard clauses at entry points
- Try-with-resources pattern instead of nested try/finally

### Atomicity Rules

1. **No void mutation methods in Service layer**
   - Services must return meaningful values or throw exceptions
   - Use `Optional<Result<T>>` patterns for partial success

2. **No nested try/catch blocks** (max depth 3)
   - Catch blocks should be flat, not nested
   - Use guard clauses to handle pre-catch conditions

3. **Fail Fast**: Invalid states throw immediately with descriptive messages
   ```java
   // GOOD: Specific exception with message
   if (!order.hasIngredients()) {
       throw new OrderValidationException(
           "Cannot process order: missing ingredients", 
           List.of("tomato", "cheese", "sauce"))
   }
```

### Simplification Rules

**Boolean Expressions**: Use ternary operators for simple boolean logic, but avoid complex nested conditions:

```java
// GOOD: Simple ternary
boolean isReady = order.isCooked() && order.isDelivered();

// BAD: Nested conditionals in one line
return (order.getAge() > 0) || !order.isCancelled();
```

**SimplifyBooleanReturn**: Return booleans directly instead of `if-else`:
```java
// GOOD
public boolean isValid(String input) {
    return input != null && !input.isEmpty();
}

// BAD (violates SimplifyBooleanExpression)
public boolean isValid(String input) {
    if (input == null || input.isEmpty()) {
        return false;
    }
    return true;
}
```

## Handling Violations

### Build Fails When:
- `mvnw clean package` runs checkstyle violations are found
- Report generated at: `target/checkstyle-result.xml`

### Workflow for Developers:

1. **Fix Immediately**: Run `mvn checkstyle:check` locally before committing
2. **Refactor, Not Ignore**: Use suppressions sparingly (only for generated code)
3. **Commit Message**: When fixing violations, note which rule was violated
4. **IDE Settings**: IDEs enforce these rules via `.editorconfig` and IntelliJ profiles

### Common Violations & Fixes:

| Error | Fix |
|-------|-----|
| Missing Javadoc on public method | Add `/** @brief description */` above declaration |
| CamelCase violation (e.g., `userName`) | Rename to `firstName` or extract field |
| Deep nesting (>3 levels) | Extract to guard clause at top of function |
| Nested try blocks | Use flat catch structure; add guards before catch |

## Checkstyle Report Structure

The generated XML report (`target/checkstyle-result.xml`) contains:
- File names and lines with violations
- Severity (error vs warning)
- Rule identifier (e.g., `TypeName`, `JavadocMethod`)
- Message explaining the violation

Example output:
```xml
<file name="/Users/GIAN/pizza-api/src/main/java/com/awesomepizza/Order.java">
  <error line="15" column="9" message="Missing Javadoc for public method" .../>
</file>
```

## IDE Enforcement

IntelliJ/Eclipse enforce these rules automatically:
- **`.idea/codeStyles/profiles/default.xml`**: IntelliJ Java code style profile
- **`.editorconfig`**: Universal format across all editors (VSCode, IntelliJ, Eclipse)

Both enforce:
- 2-space indentation (4 spaces for Java files specifically)
- Max line length: 100 characters
- Trailing whitespace removal
- Final newline on file save

## References

- [Checkstyle Rules Reference](https://checkstyle.org/config.html)
- [Maven Checkstyle Plugin](https://maven.apache.org/plugins/maven-checkstyle-plugin/)


---
**Remember**: Code quality is not a phase—it's the foundation of maintainable systems. These rules prevent bugs by design, following the **Code Philosophy**.
