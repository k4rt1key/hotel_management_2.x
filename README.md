# Concurrent Hotel Booking Test

## Overview

This tool tests the concurrent booking capabilities of the Hotel Management System, ensuring that the system properly handles race conditions when multiple users attempt to book the same rooms simultaneously.

## Test Configuration

- **Threads**: 50 concurrent users
- **Requests per Thread**: 50 booking attempts per user
- **Total Requests**: 2,500 booking attempts
- **Rooms per Request**: Random selection of 1-3 rooms

## Test Results

```
===== CONCURRENT HOTEL BOOKING TEST WITH RANDOMIZED ROOM IDs =====

----- Setting up test environment -----
Admin user setup complete
Created 50 test users
Using existing hotels with IDs: [1, 2, 3]
Using existing room IDs 1-20
Test environment setup complete

----- Loading existing rooms -----
Available rooms found in the system

----- STARTING CONCURRENT BOOKING TEST WITH RANDOMIZED ROOM IDs -----
Number of threads: 50
Booking requests per thread: 50
Total booking requests to be sent: 2500
Rooms per request: 1 to 3
```

### Sample Output

```
Thread 8 failed to book rooms [18,2,14]: Booking error: Room not available: 14
Thread 9 failed to book rooms [14]: Booking error: Room not available: 14
Thread 2 successfully booked rooms [10] from 2025-04-01T12:41:46.626973038 to 2025-04-03T14:41:46.626973038
Thread 5 successfully booked rooms [17] from 2025-04-01T12:41:46.626973038 to 2025-04-03T12:41:46.626973038
Thread 7 successfully booked rooms [11,12] from 2025-04-01T14:41:46.626973038 to 2025-04-03T16:41:46.626973038
Thread 4 successfully booked rooms [14] from 2025-04-01T13:41:46.626973038 to 2025-04-03T14:41:46.626973038
Thread 6 failed to book rooms [16,8,5]: Booking error: Could not lock room: 5
```

### Summary Results

```
===== TEST RESULTS =====
Successful bookings: 16
Failed bookings: 2484

----- Booking attempts per room -----
Room 1: 1/266 successful bookings (0.4%)
Room 2: 1/233 successful bookings (0.4%)
Room 3: 1/225 successful bookings (0.4%)
Room 4: 1/257 successful bookings (0.4%)
Room 5: 1/250 successful bookings (0.4%)
Room 6: 1/255 successful bookings (0.4%)
Room 7: 1/233 successful bookings (0.4%)
Room 8: 1/262 successful bookings (0.4%)
Room 9: 1/242 successful bookings (0.4%)
Room 10: 1/256 successful bookings (0.4%)
Room 11: 1/239 successful bookings (0.4%)
Room 12: 1/221 successful bookings (0.5%)
Room 13: 1/257 successful bookings (0.4%)
Room 14: 1/260 successful bookings (0.4%)
Room 15: 1/235 successful bookings (0.4%)
Room 16: 1/270 successful bookings (0.4%)
Room 17: 1/256 successful bookings (0.4%)
Room 18: 1/241 successful bookings (0.4%)
Room 19: 1/267 successful bookings (0.4%)
Room 20: 1/251 successful bookings (0.4%)
```

## Interpretation

The test results show that the system correctly handles concurrent booking attempts:

1. Each room has exactly 1 successful booking, demonstrating proper locking mechanisms
2. Failed bookings show appropriate error messages:
    - "Room not available" - When attempting to book an already booked room
    - "Could not lock room" - When concurrent transactions conflict

## Success Criteria

A correctly implemented system should demonstrate:

1. Each room should have either 0 or 1 successful bookings for a given time period
2. The remaining attempts should fail with appropriate error messages
3. The database should maintain consistency with no duplicate bookings
4. Multi-room bookings should either succeed for all rooms or fail for all rooms (atomicity)

## Running the Test

```java
java -classpath path/to/compiled/files Tester.Test
```