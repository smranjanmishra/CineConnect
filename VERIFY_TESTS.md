# Test Verification Guide

## Quick Test Run

Run this command to see which tests are failing:

```bash
cd C:\CineConnect\CineConnect
mvn clean test
```

## Individual Test Runs

### Test Each Service Separately:

```bash
# Test 1: CancellationService (16 tests)
mvn test -Dtest=CancellationServiceTest

# Test 2: DynamicPricingService (18 tests)
mvn test -Dtest=DynamicPricingServiceTest

# Test 3: WaitlistService (18 tests)
mvn test -Dtest=WaitlistServiceTest

# Test 4: TicketService (10 tests)
mvn test -Dtest=TicketServiceTest

# Test 5: Integration (12 tests)
mvn test -Dtest=ApplicationIntegrationTest
```

## Common Issues to Check

### 1. Missing Imports
- Check if all necessary imports are present
- Verify `java.util.Date` is imported for SeatSelection

### 2. Mock Setup
- Ensure all repositories are mocked
- Verify mock return values match expected types

### 3. Test Data
- Check if test data is properly initialized in @BeforeEach
- Verify relationships between entities (User, Show, Ticket, etc.)

## Run All Tests in IDE

1. Open IntelliJ IDEA or Eclipse
2. Right-click on `src/test/java`
3. Select "Run All Tests"
4. View test results in test runner panel

## Expected Results

- Total Tests: 74
- CancellationServiceTest: 16 tests
- DynamicPricingServiceTest: 18 tests
- WaitlistServiceTest: 18 tests
- TicketServiceTest: 10 tests
- ApplicationIntegrationTest: 12 tests

All should pass! ✅
