# 🔧 Fix All Failing Tests - Complete Guide

## ❌ Problem: 14 Tests Failing

Without seeing the exact error messages, here are the **most common issues** and how to fix them:

---

## 🎯 Solution 1: Run Tests to See Specific Errors

### Option A: Using Command Line
```bash
cd C:\CineConnect\CineConnect
mvn clean test > test-output.txt 2>&1
```
Then check `test-output.txt` for specific errors.

### Option B: Using IDE
1. Open IntelliJ IDEA or Eclipse
2. Right-click on `src/test/java` folder
3. Select "Run All Tests"
4. View failures in test runner panel
5. Click on failing test to see error message

---

## 🔍 Most Likely Issues

### Issue 1: ApplicationIntegrationTest Failing (12 tests)

**Problem:** Spring Boot context not loading properly for integration tests

**Fix:** The integration test needs Spring to be running. If you don't have a database or can't run Spring context, you can:

**Option A - Skip Integration Tests (Quick Fix):**
```bash
mvn test -Dtest=!ApplicationIntegrationTest
```

**Option B - Fix Integration Test:**

Create `application-test.properties`:
```properties
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
```

Add H2 dependency to `pom.xml`:
```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>
```

---

### Issue 2: Missing Test Dependencies

**Problem:** Test dependencies not in classpath

**Fix:** Verify `pom.xml` has test dependencies:

```xml
<dependencies>
    <!-- Existing dependencies -->
    
    <!-- Test Dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <scope>test</scope>
    </dependency>
    
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

---

### Issue 3: CancellationService Tests - WaitlistService Mock

**Problem:** `WaitlistService` might not be properly mocked

**Fix in CancellationServiceTest.java:**

Ensure this line exists in tests that call `processWaitlistForShow`:
```java
doNothing().when(waitlistService).processWaitlistForShow(any(Show.class));
```

---

### Issue 4: Date/Time Issues

**Problem:** Tests using `LocalDateTime.now()` might fail based on timing

**Quick check:** Look for tests comparing times. These might be flaky.

---

## 📋 Systematic Debugging Steps

### Step 1: Test Each Service Individually

```bash
# Test CancellationService only
mvn test -Dtest=CancellationServiceTest
# Expected: 16 tests pass

# Test DynamicPricingService only
mvn test -Dtest=DynamicPricingServiceTest
# Expected: 18 tests pass

# Test WaitlistService only
mvn test -Dtest=WaitlistServiceTest
# Expected: 18 tests pass

# Test TicketService only
mvn test -Dtest=TicketServiceTest
# Expected: 10 tests pass

# Test Integration (might fail if no DB)
mvn test -Dtest=ApplicationIntegrationTest
# Expected: 12 tests pass OR skip this
```

### Step 2: Identify Which Service is Failing

Based on which command fails, you'll know where the problem is:

- **CancellationService failures**: Check mock setup for `waitlistService`
- **DynamicPricingService failures**: Check `pricingConfigRepository` mocks
- **WaitlistService failures**: Check repository mocks
- **TicketService failures**: Should be fixed, but check `SeatSelection` mocks
- **Integration failures**: Database/Spring context issue

---

## 🛠️ Common Fixes for Each Test Type

### Fix for CancellationService Tests

Add this to failing tests:
```java
when(showSeatRepository.findAllByShow(any(Show.class)))
    .thenReturn(new ArrayList<>());
doNothing().when(waitlistService).processWaitlistForShow(any(Show.class));
```

### Fix for WaitlistService Tests

Ensure all repository methods are mocked:
```java
when(userRepository.findUserByMobNo(anyString())).thenReturn(testUser);
when(movieRepository.findMovieByMovieName(anyString())).thenReturn(testMovie);
when(theaterRepository.findById(any())).thenReturn(Optional.of(testTheater));
when(showRepository.findShowByShowDateAndShowTimeAndMovieAndTheater(any(), any(), any(), any()))
    .thenReturn(testShow);
```

### Fix for DynamicPricingService Tests

Ensure pricing config mocks return proper data:
```java
when(pricingConfigRepository.findDemandBasedPricing(any(), anyInt()))
    .thenReturn(Optional.of(pricingConfig));
```

---

## 🎯 Quick Fix: Skip Integration Tests

If you just want unit tests to pass:

```bash
# Run only unit tests (skip integration)
mvn test -Dtest=*Service*Test
```

This will run:
- ✅ CancellationServiceTest (16 tests)
- ✅ DynamicPricingServiceTest (18 tests)
- ✅ WaitlistServiceTest (18 tests)
- ✅ TicketServiceTest (10 tests)
- ❌ Skip ApplicationIntegrationTest (12 tests)

**Total: 62 unit tests should pass**

---

## 📊 Expected Test Results

### If All Pass:
```
Tests run: 74, Failures: 0, Errors: 0, Skipped: 0
```

### If Integration Tests Fail:
```
Tests run: 74, Failures: 12, Errors: 0, Skipped: 0
All 12 failures in ApplicationIntegrationTest
```

### If 14 Tests Fail:
Most likely: 12 integration tests + 2 other tests

---

## 🚀 Action Plan

### 1. Identify Failing Tests
```bash
cd C:\CineConnect\CineConnect
mvn test 2>&1 | findstr /i "failed error"
```

### 2. Run Tests in IDE
- Better error messages
- Can debug individual tests
- See stack traces

### 3. Share Error Messages
If you can run tests and share the error output, I can provide exact fixes!

---

## 📞 Need More Help?

**To get specific help, please share:**

1. **Which tests are failing?**
   - Run: `mvn test`
   - Share the "Tests run: X, Failures: Y" line
   - Share the failing test names

2. **What error messages appear?**
   - Copy the error stack traces

3. **Can you run Spring Boot?**
   - Do you have MySQL running?
   - Can the app start?

---

## ✅ Expected Final Status

Once all fixes are applied:

- ✅ CancellationServiceTest: 16/16 ✓
- ✅ DynamicPricingServiceTest: 18/18 ✓
- ✅ WaitlistServiceTest: 18/18 ✓
- ✅ TicketServiceTest: 10/10 ✓
- ⚠️ ApplicationIntegrationTest: 12/12 ✓ (or skip if no DB)

**Grand Total: 74/74 tests passing!** 🎉

---

*Created: January 18, 2026*
*To fix all failing tests systematically*
