# 🧪 CineConnect - Test Suite Quick Reference

## Quick Start

### Run All Tests
```bash
mvn test
```

### Run Specific Test
```bash
# Service Tests
mvn test -Dtest=CancellationServiceTest
mvn test -Dtest=DynamicPricingServiceTest
mvn test -Dtest=WaitlistServiceTest
mvn test -Dtest=TicketServiceTest

# Integration Test
mvn test -Dtest=ApplicationIntegrationTest
```

---

## 📊 Test Overview

| Test File | Tests | Focus | Type |
|-----------|-------|-------|------|
| CancellationServiceTest | 16 | Ticket cancellation & refunds | Unit (Mockito) |
| DynamicPricingServiceTest | 18 | Dynamic pricing calculation | Unit (Mockito) |
| WaitlistServiceTest | 18 | Waitlist management | Unit (Mockito) |
| TicketServiceTest | 10 | Ticket booking | Unit (Mockito) |
| ApplicationIntegrationTest | 12 | Spring context & wiring | Integration |

**Total: 74 Tests** ✅

---

## 🎯 Key Test Scenarios

### Cancellation Tests
- ✅ All refund percentages (100%, 75%, 50%, 25%, 0%)
- ✅ Successful cancellations
- ✅ Error handling
- ✅ Seat release
- ✅ Waitlist integration

### Pricing Tests
- ✅ Demand-based (high/medium/normal)
- ✅ Time-based (morning/afternoon/evening/night)
- ✅ Day-based (weekend/weekday)
- ✅ Combined factors
- ✅ Configuration management

### Waitlist Tests
- ✅ Add to waitlist
- ✅ Queue management (FIFO)
- ✅ Cancel waitlist
- ✅ Process notifications
- ✅ Auto-expiry

### Booking Tests
- ✅ Successful bookings
- ✅ Seat availability checks
- ✅ Price calculations
- ✅ Dynamic pricing integration

### Integration Tests
- ✅ Spring context loads
- ✅ All beans wired correctly
- ✅ Dependencies injected

---

## 📁 Test Location

```
src/test/java/com/acciojob/bookmyshowapplication/
├── service/                              (Unit Tests)
│   ├── CancellationServiceTest.java
│   ├── DynamicPricingServiceTest.java
│   ├── WaitlistServiceTest.java
│   └── TicketServiceTest.java
└── integration/                          (Integration)
    └── ApplicationIntegrationTest.java
```

---

## 🔧 Test Technologies

- **JUnit 5** - Testing framework
- **Mockito** - Mocking framework
- **Spring Boot Test** - Integration tests
- **MockitoExtension** - JUnit 5 + Mockito

---

## 📚 Documentation

- **TEST_DOCUMENTATION.md** - Complete test documentation
- **TEST_REORGANIZATION_COMPLETE.md** - Reorganization summary
- **README_TESTS.md** - This quick reference

---

## ✅ Test Quality

- ✅ 100% using Mockito for unit tests
- ✅ 100% sequential ordering
- ✅ 100% descriptive names
- ✅ 100% AAA pattern
- ✅ 100% isolated tests

---

**All tests pass with 100% success rate!** 🎉
