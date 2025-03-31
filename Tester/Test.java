package Tester;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class Test
{
    private static final int NUM_THREADS = 50;

    private static final int NUM_REQUESTS_PER_THREAD = 50;

    private static final String SERVER_HOST = "localhost";

    private static final int SERVER_PORT = 8081;

    private static final int MIN_ROOMS_PER_REQUEST = 1;

    private static final int MAX_ROOMS_PER_REQUEST = 3;

    private static final String ADMIN_USERNAME = "admin";

    private static final String ADMIN_PASSWORD = "admin";

    private static final String TEST_USER_PREFIX = "test";

    private static final String TEST_PASSWORD = "test";

    private static final Random random = new Random(System.currentTimeMillis());

    private static final List<String> existingRoomIds = new ArrayList<>();

    private static final List<String> existingHotelIds = List.of("1", "2", "3");

    private static final AtomicInteger successfulBookings = new AtomicInteger(0);

    private static final AtomicInteger failedBookings = new AtomicInteger(0);

    private static final HashMap<String, Integer> roomBookingAttempts = new HashMap<>();

    private static final HashMap<String, Integer> roomBookingSuccesses = new HashMap<>();

    public static void main(String[] args)
    {
        try
        {
            System.out.println("===== CONCURRENT HOTEL BOOKING TEST WITH RANDOMIZED ROOM IDs =====");

            setupTestEnvironment();

            loadExistingRooms();

            runConcurrentBookingTest();

            printResults();
        }
        catch (Exception e)
        {
            System.err.println("Test failed with exception: " + e.getMessage());

            e.printStackTrace();
        }
    }

    private static void setupTestEnvironment() throws Exception
    {
        System.out.println("\n----- Setting up test environment -----");

        sendRequest(createRequestMap("create_user", ADMIN_USERNAME, ADMIN_PASSWORD));

        System.out.println("Admin user setup complete");

        for (int i = 0; i < NUM_THREADS; i++)
        {
            String username = TEST_USER_PREFIX + i;

            sendRequest(createRequestMap("create_user", username, TEST_PASSWORD));
        }

        System.out.println("Created " + NUM_THREADS + " test users");

        System.out.println("Using existing hotels with IDs: " + existingHotelIds);

        for (int i = 1; i <= 20; i++)
        {
            String roomId = String.valueOf(i);

            existingRoomIds.add(roomId);

            roomBookingAttempts.put(roomId, 0);

            roomBookingSuccesses.put(roomId, 0);
        }

        System.out.println("Using existing room IDs 1-20");

        System.out.println("Test environment setup complete");
    }

    private static void loadExistingRooms() throws Exception
    {
        System.out.println("\n----- Loading existing rooms -----");

        HashMap<String, String> availabilityRequest = createRequestMap(
                "check_availability",
                ADMIN_USERNAME,
                ADMIN_PASSWORD);

        LocalDateTime checkInTime = LocalDateTime.now().plusDays(1);

        LocalDateTime checkOutTime = checkInTime.plusDays(2);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        availabilityRequest.put("checkInTime", checkInTime.format(formatter));

        availabilityRequest.put("checkOutTime", checkOutTime.format(formatter));

        HashMap<String, String> response = sendRequest(availabilityRequest);

        if ("true".equals(response.get("success")))
        {
            System.out.println("Available rooms found in the system");
        }
        else
        {
            System.out.println("Note: No rooms available for chosen dates. Using predefined room IDs.");
        }
    }

    private static void runConcurrentBookingTest() throws Exception
    {
        LocalDateTime baseCheckInTime = LocalDateTime.now().plusDays(1);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        System.out.println("\n----- STARTING CONCURRENT BOOKING TEST WITH RANDOMIZED ROOM IDs -----");

        System.out.println("Number of threads: " + NUM_THREADS);

        System.out.println("Booking requests per thread: " + NUM_REQUESTS_PER_THREAD);

        System.out.println("Total booking requests to be sent: " + (NUM_THREADS * NUM_REQUESTS_PER_THREAD));

        System.out.println("Rooms per request: " + MIN_ROOMS_PER_REQUEST + " to " + MAX_ROOMS_PER_REQUEST);

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);

        CountDownLatch latch = new CountDownLatch(NUM_THREADS);

        for (int i = 0; i < NUM_THREADS; i++)
        {
            final int userId = i;

            executor.submit(() ->
            {
                try
                {
                    for (int j = 0; j < NUM_REQUESTS_PER_THREAD; j++)
                    {
                        int hourVariation = random.nextInt(3);

                        LocalDateTime checkInTime = baseCheckInTime.plusHours(hourVariation);

                        LocalDateTime checkOutTime = checkInTime.plusDays(2).plusHours(random.nextInt(3));

                        String roomIdsForBooking = generateRandomRoomIds();

                        HashMap<String, String> bookingRequest = createRequestMap(
                                "book_rooms",
                                TEST_USER_PREFIX + userId,
                                TEST_PASSWORD);

                        bookingRequest.put("roomIds", roomIdsForBooking);

                        bookingRequest.put("checkInTime", checkInTime.format(formatter));

                        bookingRequest.put("checkOutTime", checkOutTime.format(formatter));

                        bookingRequest.put("paymentMethod", random.nextBoolean() ? "UPI" : "CASH");

                        String[] roomIdsArray = roomIdsForBooking.split(",");

                        synchronized (roomBookingAttempts)
                        {
                            for (String roomId : roomIdsArray)
                            {
                                roomBookingAttempts.put(roomId, roomBookingAttempts.get(roomId) + 1);
                            }
                        }

                        HashMap<String, String> response = sendRequest(bookingRequest);

                        if ("true".equals(response.get("success")))
                        {
                            System.out.println("Thread " + userId + " successfully booked rooms [" + roomIdsForBooking + "]" +
                                    " from " + checkInTime.format(formatter) + " to " + checkOutTime.format(formatter));

                            successfulBookings.incrementAndGet();

                            synchronized (roomBookingSuccesses)
                            {
                                for (String roomId : roomIdsArray)
                                {
                                    roomBookingSuccesses.put(roomId, roomBookingSuccesses.get(roomId) + 1);
                                }
                            }
                        }
                        else
                        {
                            System.out.println("Thread " + userId + " failed to book rooms [" + roomIdsForBooking + "]" +
                                    ": " + response.get("message"));

                            failedBookings.incrementAndGet();
                        }

                        Thread.sleep(random.nextInt(100));
                    }
                }
                catch (Exception e)
                {
                    System.err.println("Error in thread " + userId + ": " + e.getMessage());
                }
                finally
                {
                    latch.countDown();
                }
            });
        }

        latch.await();

        executor.shutdown();

        System.out.println("All booking threads completed execution");
    }

    private static String generateRandomRoomIds()
    {
        int numRoomsToBook = random.nextInt(MAX_ROOMS_PER_REQUEST - MIN_ROOMS_PER_REQUEST + 1) + MIN_ROOMS_PER_REQUEST;

        List<String> shuffledRoomIds = new ArrayList<>(existingRoomIds);

        Collections.shuffle(shuffledRoomIds, random);

        List<String> selectedRoomIds = shuffledRoomIds.subList(0, numRoomsToBook);

        return String.join(",", selectedRoomIds);
    }

    private static void printResults()
    {
        System.out.println("\n===== TEST RESULTS =====");

        System.out.println("Successful bookings: " + successfulBookings.get());

        System.out.println("Failed bookings: " + failedBookings.get());

        System.out.println("\n----- Booking attempts per room -----");

        for (String roomId : existingRoomIds)
        {
            int attempts = roomBookingAttempts.get(roomId);

            int successes = roomBookingSuccesses.get(roomId);

            if (attempts > 0)
            {
                System.out.printf("Room %s: %d/%d successful bookings (%.1f%%)\n",
                        roomId, successes, attempts, (successes * 100.0 / attempts));
            }
            else
            {
                System.out.printf("Room %s: No booking attempts\n", roomId);
            }
        }

        System.out.println("\nConcurrency test complete. If your system correctly handles concurrent bookings:");

        System.out.println("1. Each room should have either 0 or 1 successful bookings for a given time period");

        System.out.println("2. The remaining attempts should have failed with appropriate error messages");

        System.out.println("3. The database should maintain consistency with no duplicate bookings");

        System.out.println("4. Multi-room bookings should either succeed for all rooms or fail for all rooms (atomicity)");
    }

    private static HashMap<String, String> createRequestMap(String type, String username, String password)
    {
        HashMap<String, String> request = new HashMap<>();

        request.put("type", type);

        request.put("username", username);

        request.put("password", password);

        return request;
    }

    private static HashMap<String, String> createRequestMap(String type, String username, String password, String... keyValues)
    {
        HashMap<String, String> request = createRequestMap(type, username, password);

        for (int i = 0; i < keyValues.length; i += 2)
        {
            if (i + 1 < keyValues.length)
            {
                request.put(keyValues[i], keyValues[i + 1]);
            }
        }

        return request;
    }

    private static HashMap<String, String> sendRequest(HashMap<String, String> request) throws Exception
    {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream input = new ObjectInputStream(socket.getInputStream()))
        {
            output.writeObject(request);

            output.flush();

            @SuppressWarnings("unchecked")
            HashMap<String, String> response = (HashMap<String, String>) input.readObject();

            return response;
        }
    }
}