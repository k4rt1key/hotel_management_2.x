import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class Client
{
    private String username;

    private String password;

    private boolean isAdmin;

    private final Scanner scanner = new Scanner(System.in);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    public void start() {

        displayWelcomeBanner();

        performAuthentication();
    }

    private void displayWelcomeBanner()
    {
        System.out.println("🏨 Welcome to Luxe Hotel Booking System 🌟");
        System.out.println("Your gateway to comfortable stays around the world! 🌍");
    }

    private void performAuthentication()
    {

        while (true) {

            System.out.println("\n📋 Authentication Menu:");
            System.out.println("1. 🔐 Login");
            System.out.println("2. 🆕 Create Account");
            System.out.println("3. 🚪 Exit");
            System.out.print("Choose an option: ");

            var choice = getUserChoice(3);

            switch (choice) {
                case 1 -> performLogin();
                case 2 -> createAccount();
                case 3 ->
                {
                    System.out.println("👋 Goodbye! Thank you for using Hotel Booking System.");
                    System.exit(0);
                }
            }
        }
    }

    private void performLogin()
    {

        System.out.print("👤 Enter Username: ");
        var inputUsername = scanner.nextLine().trim();

        System.out.print("🔑 Enter Password: ");
        var inputPassword = scanner.nextLine().trim();

        var request = new HashMap<String, String>();
        request.put("type", "login");
        request.put("username", inputUsername);
        request.put("password", inputPassword);

       var response = sendRequest(request);

        if (response != null && "true".equals(response.get("success")))
        {
            username = inputUsername;
            password = inputPassword;
            isAdmin = "admin".equals(username);

            System.out.println("🎉 " + response.get("message"));

            showMainMenu();
        }
        else
        {
            System.out.println("❌ Login failed: " + (response != null ? response.get("message") : "Server error"));
        }
    }

    private void createAccount()
    {
        System.out.print("👤 Choose Username: ");
        var newUsername = scanner.nextLine().trim();

        System.out.print("🔑 Choose Password: ");
        var newPassword = scanner.nextLine().trim();

        var request = new HashMap<String, String>();
        request.put("type", "create_user");
        request.put("username", newUsername);
        request.put("password", newPassword);

        HashMap<String, String> response = sendRequest(request);

        if (response != null && "true".equals(response.get("success")))
        {
            System.out.println("✅ Account created successfully!");

            username = newUsername;
            password = newPassword;
            isAdmin = false;

            showMainMenu();
        }
        else
        {
            System.out.println("❌ Account creation failed: " + (response != null ? response.get("message") : "Server error"));
        }
    }

    private void showMainMenu()
    {
        while (!Thread.currentThread().isInterrupted())
        {
            System.out.println("\n📋 Main Menu");

            if (isAdmin)
            {
                showAdminMenu();
            }
            else
            {
                showUserMenu();
            }
        }
    }

    private void showAdminMenu()
    {

        System.out.println("1. 🏨 Hotel Management");
        System.out.println("2. 🛏️ Room Management");
        System.out.println("3. 👥 User Management");
        System.out.println("4. 📋 View All Transactions");
        System.out.println("5. 📊 Transaction Reports");
        System.out.println("6. 🚪 Logout");
        System.out.print("Choose an option: ");

        var choice = getUserChoice(7);

        switch (choice)
        {
            case 1 -> hotelManagement();
            case 2 -> roomManagement();
            case 3 -> userManagement();
            case 4 -> viewAllTransactions();
            case 5 -> transactionReports();
            case 6 -> logout();
        }
    }

    private void showUserMenu()
    {
        System.out.println("1. 🔍 Check Room Availability");
        System.out.println("2. 🏨 Book a Room");
        System.out.println("3. 📋 View My Bookings");
        System.out.println("4. 🔐 Change Password");
        System.out.println("5. 🚪 Logout");
        System.out.print("Choose an option: ");

        var choice = getUserChoice(5);

        switch (choice)
        {
            case 1 -> checkRoomAvailability();
            case 2 -> bookRoom();
            case 3 -> viewMyBookings();
            case 4 -> changePassword();
            case 5 -> logout();
        }
    }

    private void hotelManagement()
    {
        System.out.println("\n🏨 Hotel Management");
        System.out.println("1. 🆕 Create Hotel");
        System.out.println("2. ✏️ Update Hotel");
        System.out.println("3. ❌ Remove Hotel");
        System.out.println("4. 📋 List Hotels");
        System.out.println("5. 🔙 Back");
        System.out.print("Choose an option: ");

        var choice = getUserChoice(5);

        switch (choice)
        {
            case 1 -> createHotel();
            case 2 -> updateHotel();
            case 3 -> removeHotel();
            case 4 -> listHotels();
            case 5 -> {}
        }
    }

    private void roomManagement()
    {
        System.out.println("\n🛏️ Room Management");
        System.out.println("1. 🆕 Create Room");
        System.out.println("2. ✏️ Update Room");
        System.out.println("3. 📋 List Rooms");
        System.out.println("4. 🔙 Back");
        System.out.print("Choose an option: ");

        var choice = getUserChoice(4);

        switch (choice)
        {
            case 1 -> createRoom();
            case 2 -> updateRoom();
            case 3 -> listHotels();
            case 4 -> {}
        }
    }

    private void userManagement()
    {
        System.out.println("\n👥 User Management");
        System.out.println("1. 📋 List Users");
        System.out.println("2. ❌ Remove User");
        System.out.println("3. 🔙 Back");
        System.out.print("Choose an option: ");

        var choice = getUserChoice(3);

        switch (choice)
        {
            case 1 -> listUsers();
            case 2 -> removeUser();
            case 3 -> {}
        }
    }

    private void transactionReports()
    {
        System.out.println("\n📊 Transaction Reports");
        System.out.println("1. 📅 View Transactions by Time Range");
        System.out.println("2. 👤 View User Transactions by Time Range");
        System.out.println("3. 🔙 Back");
        System.out.print("Choose an option: ");

        var choice = getUserChoice(3);

        switch (choice)
        {
            case 1 -> viewTransactionsByTimeRange();
            case 2 -> viewUserTransactionsByTimeRange();
            case 3 -> {}
        }
    }

    private int getUserChoice(int maxChoice)
    {
        while (true)
        {
            try
            {
                var choice = Integer.parseInt(scanner.nextLine().trim());

                if (choice >= 1 && choice <= maxChoice)
                {
                    return choice;
                }

                System.out.print("❌ Invalid choice. Please enter a number between 1 and " + maxChoice + ": ");
            }
            catch (NumberFormatException e)
            {
                System.out.print("❌ Please enter a valid number: ");
            }
        }
    }

    private void createHotel()
    {

        System.out.print("🏨 Enter Hotel Name: ");
        var hotelName = scanner.nextLine().trim();

        var request = new HashMap<String, String>();
        request.put("type", "create_hotel");
        request.put("username", username);
        request.put("password", password);
        request.put("name", hotelName);

        HashMap<String, String> response = sendRequest(request);

        handleServerResponse(response, "Hotel created successfully! 🎉");
    }

    private void updateHotel()
    {
        listHotels();

        System.out.print("🏨 Enter Hotel ID to update: ");
        var hotelId = scanner.nextLine().trim();

        System.out.print("✏️ Enter New Hotel Name: ");
        var newName = scanner.nextLine().trim();

       var request = new HashMap<String, String>();
        request.put("type", "update_hotel");
        request.put("username", username);
        request.put("password", password);
        request.put("hotelId", hotelId);
        request.put("name", newName);

        HashMap<String, String> response = sendRequest(request);

        handleServerResponse(response, "Hotel updated successfully! 🎉");
    }

    private void removeHotel()
    {

        listHotels();

        System.out.print("🏨 Enter Hotel ID to remove: ");
        var hotelId = scanner.nextLine().trim();

        var request = new HashMap<String, String>();
        request.put("type", "remove_hotel");
        request.put("username", username);
        request.put("password", password);
        request.put("hotelId", hotelId);

        var response = sendRequest(request);

        handleServerResponse(response, "Hotel removed successfully! 🎉");
    }

    private void listHotels()
    {

        var request = new HashMap<String, String>();
        request.put("type", "list_hotels");
        request.put("username", username);
        request.put("password", password);

        var response = sendRequest(request);

        handleServerResponse(response, null);
    }

    private void createRoom()
    {

        listHotels();

        System.out.print("🏨 Enter Hotel Name: ");
        String hotelName = scanner.nextLine().trim();

        System.out.print("🚪 Enter Room Number: ");
        String roomNumber = scanner.nextLine().trim();

        System.out.print("🏷️ Enter Room Type (SINGLE/DOUBLE/DELUX/SUITE): ");
        String roomType = scanner.nextLine().trim().toUpperCase();

        System.out.print("💰 Enter Room Price: ");
        String price = scanner.nextLine().trim();

        var request = new HashMap<String, String>();

        request.put("type", "create_room");
        request.put("username", username);
        request.put("password", password);
        request.put("hotelName", hotelName);
        request.put("roomNumber", roomNumber);
        request.put("roomType", roomType);
        request.put("price", price);

        var response = sendRequest(request);

        handleServerResponse(response, "Room created successfully! 🎉");
    }

    private void updateRoom()
    {

        listHotels();

        System.out.print("🛏️ Enter Room ID to update: ");
        var roomId = scanner.nextLine().trim();

        System.out.print("🚪 Enter New Room Number (or press Enter to skip): ");
        var roomNumber = scanner.nextLine().trim();

        System.out.print("💰 Enter New Price (or press Enter to skip): ");
        var price = scanner.nextLine().trim();

        var request = new HashMap<String, String>();
        request.put("type", "update_room");
        request.put("username", username);
        request.put("password", password);
        request.put("roomId", roomId);

        if (!roomNumber.isEmpty()) request.put("roomNumber", roomNumber);

        if (!price.isEmpty()) request.put("price", price);

        var response = sendRequest(request);

        handleServerResponse(response, "Room updated successfully! 🎉");
    }

    private void listUsers()
    {

        var request = new HashMap<String, String>();
        request.put("type", "list_user");
        request.put("username", username);
        request.put("password", password);

        var response = sendRequest(request);

        assert response != null;
        handleServerResponse(response, response.get("message"));
    }

    private void removeUser()
    {

        System.out.print("👤 Enter Username to remove: ");

        var userToRemove = scanner.nextLine().trim();

        var request = new HashMap<String, String>();
        request.put("type", "remove_user");
        request.put("username", username);
        request.put("password", password);
        request.put("username_to_remove", userToRemove);

        var response = sendRequest(request);

        handleServerResponse(response, "User removed successfully! 🎉");
    }

    private LocalDateTime getDateTimeInput(String prompt)
    {

        while (true)
        {

            System.out.println("\n" + prompt);

            System.out.print("📅 Enter Day (1-31): ");
            var day = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("📅 Enter Month (1-12): ");
            var month = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("📅 Enter Year (e.g., 2025): ");
            var year = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("⏰ Enter Hour (0-23): ");
            var hour = Integer.parseInt(scanner.nextLine().trim());

            var minute = 0;

            try
            {
                return LocalDateTime.of(year, month, day, hour, minute);
            }
            catch (Exception e)
            {
                System.out.println("❌ Invalid date/time input. Please try again.");
            }
        }
    }

    private void checkRoomAvailability()
    {

        var checkIn = getDateTimeInput("Enter Check-in Date and Time");

        var checkOut = getDateTimeInput("Enter Check-out Date and Time");

        if (!checkOut.isAfter(checkIn))
        {
            System.out.println("❌ Check-out time must be after check-in time!");
            return;
        }

        var request = new HashMap<String, String>();
        request.put("type", "check_availability");
        request.put("username", username);
        request.put("password", password);
        request.put("checkInTime", checkIn.format(DATE_FORMATTER));
        request.put("checkOutTime", checkOut.format(DATE_FORMATTER));

        var response = sendRequest(request);

        handleServerResponse(response, null);
    }

    private void bookRoom()
    {
        var checkIn = getDateTimeInput("Enter Check-in Date and Time");

        var checkOut = getDateTimeInput("Enter Check-out Date and Time");

        if (!checkOut.isAfter(checkIn))
        {
                System.out.println("❌ Check-out time must be after check-in time!");
                return;
        }

        var request = new HashMap<String, String>();

        request.put("type", "check_availability");
        request.put("username", username);
        request.put("password", password);
        request.put("checkInTime", checkIn.format(DATE_FORMATTER));
        request.put("checkOutTime", checkOut.format(DATE_FORMATTER));

        var response = sendRequest(request);

        if (response != null && "true".equals(response.get("success")))
        {

            System.out.println("\nAvailable Rooms:");

            response.forEach((key, value) ->
            {
                if (!key.equals("success") && !key.equals("status") && !key.equals("message"))
                {
                    System.out.println(key + ": " + value);
                }
            });

            System.out.print("\n🛏️ Enter Room IDs to book (comma-separated): ");

            String roomIds = scanner.nextLine().trim();

            System.out.println("\n💳 Select Payment Method:");
            System.out.println("1. UPI");
            System.out.println("2. Cash");
            System.out.print("Choose payment method: ");

            var paymentChoice = getUserChoice(2);

            var paymentMethod = paymentChoice == 1 ? "UPI" : "CASH";

            request = new HashMap<>();
            request.put("type", "book_rooms");
            request.put("username", username);
            request.put("password", password);
            request.put("roomIds", roomIds);
            request.put("checkInTime", checkIn.format(DATE_FORMATTER));
            request.put("checkOutTime", checkOut.format(DATE_FORMATTER));
            request.put("paymentMethod", paymentMethod);

            response = sendRequest(request);

            handleServerResponse(response, "Rooms booked successfully! 🎉");
        }
        else
        {
            System.out.println("\n❌ No rooms available for the selected dates.");
        }
    }

    private void viewMyBookings()
    {
        var request = new HashMap<String, String>();

        request.put("type", "list_user_transactions");
        request.put("username", username);
        request.put("password", password);

        var response = sendRequest(request);

        handleServerResponse(response, null);
    }

    private void viewAllTransactions()
    {

        var request = new HashMap<String, String>();

        request.put("type", "list_all_transactions");
        request.put("username", username);
        request.put("password", password);

        var response = sendRequest(request);

        handleServerResponse(response, null);
    }

    private void viewTransactionsByTimeRange()
    {
        var fromTime = getDateTimeInput("Enter Start Date and Time");

        var toTime = getDateTimeInput("Enter End Date and Time");

        var request = new HashMap<String, String>();
        request.put("type", "list_all_transactions_by_time");
        request.put("username", username);
        request.put("password", password);
        request.put("fromTime", fromTime.format(DATE_FORMATTER));
        request.put("toTime", toTime.format(DATE_FORMATTER));

        var response = sendRequest(request);

        handleServerResponse(response, null);
    }

    private void viewUserTransactionsByTimeRange()
    {
        System.out.print("\n👤 Enter Username: ");
        var targetUsername = scanner.nextLine().trim();

        var fromTime = getDateTimeInput("Enter Start Date and Time");

        var toTime = getDateTimeInput("Enter End Date and Time");

        var request = new HashMap<String, String>();
        request.put("type", "list_user_transactions_by_time");
        request.put("username", username);
        request.put("password", password);
        request.put("targetUsername", targetUsername);
        request.put("fromTime", fromTime.format(DATE_FORMATTER));
        request.put("toTime", toTime.format(DATE_FORMATTER));

        var response = sendRequest(request);

        handleServerResponse(response, null);
    }

    private void changePassword()
    {

        System.out.print("🔑 Enter Current Password: ");
        String currentPassword = scanner.nextLine().trim();

        System.out.print("🔑 Enter New Password: ");
        String newPassword = scanner.nextLine().trim();

        var request = new HashMap<String, String>();
        request.put("type", "change_password");
        request.put("username", username);
        request.put("password", currentPassword);
        request.put("newPassword", newPassword);

        var response = sendRequest(request);

        if (response != null && "true".equals(response.get("success")))
        {
            password = newPassword;

            System.out.println("✅ " + response.get("message"));
        }
        else
        {
            handleServerResponse(response, "Password changed successfully! 🎉");
        }
    }

    private void logout()
    {
        username = null;

        password = null;

        isAdmin = false;

        System.out.println("👋 Logged out successfully!");

        performAuthentication();
    }

    private void handleServerResponse(HashMap<String, String> response, String successMessage)
    {
        if (response == null)
        {
            System.out.println("❌ Server communication error.");

            return;
        }

        if ("true".equals(response.get("success")))
        {
            if (successMessage != null)
            {
                System.out.println(successMessage);
            }

            response.forEach((key, value) ->
            {
                if (!key.equals("success") && !key.equals("status") && !key.equals("message"))
                {
                    System.out.println(key + ": " + value);
                }
            });

        }
        else
        {
            System.out.println("❌ " + response.get("message"));
        }
    }

    private HashMap<String, String> sendRequest(HashMap<String, String> request) {

        final var HOST = "localhost";

        final var PORT = 8081;

        try (
                var client = new Socket(HOST, PORT);
                var serverWriter = new ObjectOutputStream(client.getOutputStream());
                var serverReader = new ObjectInputStream(client.getInputStream())
        )
        {

            serverWriter.writeObject(request);

            serverWriter.flush();

            @SuppressWarnings("unchecked")
            var response = (HashMap<String, String>) serverReader.readObject();

            return response;

        }
        catch (Exception e) {

            System.out.println("❌ Error communicating with server: " + e.getMessage());

            return null;
        }
    }

    public static void main(String[] args) {

        new Client().start();
    }
}