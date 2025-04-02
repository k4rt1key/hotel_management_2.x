import Database.Database;
import Schema.Room;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Collectors;

public class Worker implements Runnable
{
    private final Socket client;

    private final HashMap<String, String> response;

    // Constructor
    Worker(Socket client)
    {
        this.client = client;

        this.response = new HashMap<>();
    }

    @Override
    public void run()
    {
        try (
                var clientReader = new ObjectInputStream(client.getInputStream());

                var clientWriter = new ObjectOutputStream(client.getOutputStream())
        )
        {
            @SuppressWarnings("unchecked")
            var request = (HashMap<String, String>) clientReader.readObject();

            var user = Database.getUser(request.get("username"));

            // Authentication check
            if ( !Objects.equals(request.get("type"), "create_user") && (user == null || !user.validatePassword(request.get("password"))))
            {
                sendUnauthorizedResponse(clientWriter, "Wrong credentials");

                return;
            }

            switch (request.get("type"))
            {
                case "login":

                    sendSuccessResponse(clientWriter, "Welcome back " + request.get("username"));

                    return;

                case "create_user":

                    user = Database.createUser(request.get("username"), request.get("password"));

                    Database.users.put(request.get("username"), user);

                    sendSuccessResponse(clientWriter, "Welcome " + user.getUsername());

                    return;

                case "remove_user":

                    user = Database.removeUser(request.get("username_to_remove"));

                    if (user != null)
                    {
                        sendSuccessResponse(clientWriter, "Removed " + user.getUsername());
                    }
                    else
                    {
                        sendUnauthorizedResponse(clientWriter, "Wrong credentials");
                    }
                    return;

                case "list_user":

                    if (!user.isAdmin())
                    {
                        sendUnauthorizedResponse(clientWriter, "You are not privileged to do this operation");
                    }
                    else
                    {
                        var users = Database.users.values();

                        var usersStr = users.stream()

                                .map(Object::toString)

                                .collect(Collectors.joining("\n"));

                        sendSuccessResponse(clientWriter, "All users...\n" + usersStr);
                    }

                    return;

                case "change_password":

                    user = Database.changePassword(request.get("username"), request.get("password"), request.get("newPassword"));

                    if (user == null)
                    {
                        sendUnauthorizedResponse(clientWriter, "Wrong credentials");
                    }
                    else
                    {
                        sendSuccessResponse(clientWriter, "Changed password successfully");
                    }
                    return;

                case "create_hotel":

                    if (!user.isAdmin())
                    {
                        sendUnauthorizedResponse(clientWriter, "You are not privileged to do this operation");

                        return;
                    }

                    var hotel = Database.createHotel(request.get("name"));

                    Database.hotels.put(hotel.getId(), hotel);

                    sendSuccessResponse(clientWriter, "Hotel created successfully", "hotel", hotel.toString());

                    return;

                case "list_hotels":

                    if (Database.hotels.isEmpty())
                    {
                        sendErrorResponse(clientWriter, "404", "No hotels found");
                    }
                    else
                    {
                        StringBuilder hotelsBuilder = new StringBuilder();

                        hotelsBuilder.append("===== HOTELS =====\n\n");

                        for (var dbHotel : Database.hotels.values())
                        {
                            hotelsBuilder.append(dbHotel.toString()).append("\n\n");
                        }

                        sendSuccessResponse(clientWriter, "All hotels", "hotels", hotelsBuilder.toString());
                    }
                    return;

                case "remove_hotel":

                    if (!user.isAdmin())
                    {
                        sendUnauthorizedResponse(clientWriter, "You are not privileged to do this operation");

                        return;
                    }

                    var removedHotel = Database.removeHotel(request.get("hotelId"));

                    if (removedHotel != null)
                    {
                        sendSuccessResponse(clientWriter, "Hotel removed successfully");
                    }
                    else
                    {
                        sendErrorResponse(clientWriter, "404", "Hotel not found");
                    }
                    return;

                case "update_hotel":

                    if (!user.isAdmin())
                    {
                        sendUnauthorizedResponse(clientWriter, "You are not privileged to do this operation");

                        return;
                    }

                    var updatedHotel = Database.updateHotel(request.get("hotelId"), request.get("name"));

                    sendSuccessResponse(clientWriter, "Hotel updated successfully", "hotel", updatedHotel.toString());

                    return;

                case "create_room":

                    if (!user.isAdmin())
                    {
                        sendUnauthorizedResponse(clientWriter, "You are not privileged to do this operation");

                        return;
                    }


                    var hotelForRoom = Database.hotels.get(request.get("hotelName"));

                    if (hotelForRoom == null)
                    {
                        sendErrorResponse(clientWriter, "404", "Hotel not found");

                        return;
                    }

                    var room = Database.createRoom(
                            request.get("roomNumber"),
                            Integer.parseInt(request.get("price")),
                            request.get("roomType"),
                            hotelForRoom
                    );

                    Database.rooms.put(room.getId(), room);

                    sendSuccessResponse(clientWriter, "Room created successfully", "room", room.toString());

                    return;

                case "update_room":
                    if (!user.isAdmin())
                    {
                        sendUnauthorizedResponse(clientWriter, "You are not privileged to do this operation");

                        return;
                    }

                    var updatedRoom = Database.updateRoom(
                            request.get("roomId"),
                            request.get("roomNumber"),
                            request.get("price") != null ? Integer.parseInt(request.get("price")) : null
                    );

                    if (updatedRoom != null)
                    {
                        sendSuccessResponse(clientWriter, "Room updated successfully", "room", updatedRoom.toString());
                    }
                    else
                    {
                        sendErrorResponse(clientWriter, "404", "Room not found");
                    }

                    return;

                case "check_availability":

                    var availableRooms = Database.getAvailableRooms(
                            LocalDateTime.parse(request.get("checkInTime")),
                            LocalDateTime.parse(request.get("checkOutTime"))
                    );

                    if (availableRooms.isEmpty())
                    {
                        sendErrorResponse(clientWriter, "404", "No rooms available");
                    }
                    else
                    {
                        var availableRoomsStr = availableRooms
                                .values()

                                .stream()

                                .map(Object::toString)

                                .collect(Collectors.joining("\n"));

                        sendSuccessResponse(clientWriter, "Available rooms", "rooms", availableRoomsStr);
                    }

                    return;

                case "book_rooms":

                    if(request.get("roomIds") == null)
                    {
                        sendErrorResponse(clientWriter, "404", "Booking failed. Send atleast one room to book.");
                        return;
                    }
                    var roomIds = request.get("roomIds").split(",");

                    var roomsToBook = java.util.Arrays
                            .stream(roomIds)

                            .map(Database::getRoom)

                            .collect(Collectors.toList());

                    try
                    {
                        var bookings = Database.bookRooms(
                                user,
                                roomsToBook,
                                request.get("checkInTime"),
                                request.get("checkOutTime"),
                                request.get("paymentMethod")
                        );

                        if (bookings == null)
                        {
                            sendErrorResponse(clientWriter, "400", "Booking failed. Rooms might be unavailable.");
                        }
                        else
                        {
                            var bookingsStr = bookings.values()
                                    .stream()

                                    .map(Object::toString)

                                    .collect(Collectors.joining("\n"));

                            sendSuccessResponse(clientWriter, "Rooms booked successfully", "bookings", bookingsStr);
                        }
                    }
                    catch (Exception e)
                    {
                        sendErrorResponse(clientWriter, "500", "Booking error: " + e.getMessage());
                    }
                    return;

                case "list_user_transactions":

                    var userTransactions = Database.getTransactions(user);

                    if (userTransactions.isEmpty())
                    {
                        sendErrorResponse(clientWriter, "404", "No transactions found");
                    }
                    else
                    {
                        var transactionsDetails = new StringBuilder();

                        transactionsDetails.append("===== YOUR TRANSACTIONS =====\n\n");

                        for (var transaction : userTransactions.values())
                        {
                            // Get all bookings for this transaction
                            var transactionBookings = Database.bookings
                                    .values()
                                    .stream()
                                    .filter(booking -> booking.getTransaction().getId().equals(transaction.getId()))
                                    .toList();

                            transactionsDetails.append(transaction.toString()).append("\n");

                            transactionsDetails.append("    Bookings (").append(transactionBookings.size()).append("): [\n");

                            if (transactionBookings.isEmpty())
                            {
                                transactionsDetails.append("        No bookings found for this transaction\n");
                            }
                            else
                            {
                                for (var booking : transactionBookings)
                                {
                                    transactionsDetails.append("        ").append(booking.toString().replace("\n", "\n        ")).append("\n");

                                    // Add room details
                                    Room bookingRoom = booking.getRoom();

                                    transactionsDetails.append("            Room Details: ").append(bookingRoom.getRoomNumber())
                                            .append(" (").append(bookingRoom.getType()).append(") at ")
                                            .append(bookingRoom.getHotel().getName())
                                            .append(" - $").append(bookingRoom.getPrice()).append("\n");
                                }
                            }

                            transactionsDetails.append("    ]\n");

                            transactionsDetails.append("\n\n ==== X ====\n\n");

                        }

                        sendSuccessResponse(
                                clientWriter,
                                "User transactions",
                                "transactions",
                                transactionsDetails.toString()
                        );
                    }

                    return;

                case "list_user_transactions_by_time":

                    if (!user.isAdmin())
                    {
                        sendUnauthorizedResponse(clientWriter, "You are not privileged to do this operation");

                        return;
                    }

                    try
                    {
                        var targetUser = Database.getUser(request.get("targetUsername"));

                        if (targetUser == null)
                        {
                            sendErrorResponse(clientWriter, "404", "User not found");

                            return;
                        }

                        var timeFilteredUserTransactions = Database.getTransactions(
                                targetUser,
                                LocalDateTime.parse(request.get("fromTime")),
                                LocalDateTime.parse(request.get("toTime"))
                        );

                        if (timeFilteredUserTransactions.isEmpty())
                        {
                            sendErrorResponse(clientWriter, "404", "No transactions found for this user in the specified time range");
                        }
                        else
                        {
                            StringBuilder transactionsDetails = new StringBuilder();

                            transactionsDetails
                                    .append("===== TRANSACTIONS FOR USER ")
                                    .append(request.get("targetUsername"))
                                    .append(" =====\n")
                                    .append("From: ").append(request.get("fromTime"))
                                    .append(" To: ").append(request.get("toTime"))
                                    .append("\n\n");

                            for (var transaction : timeFilteredUserTransactions.values())
                            {
                                // Get all bookings for this transaction
                                var transactionBookings = Database.bookings.values()
                                        .stream()
                                        .filter(booking -> booking.getTransaction().getId().equals(transaction.getId()))
                                        .toList();

                                transactionsDetails.append(transaction.toString()).append("\n");

                                transactionsDetails.append("    Bookings (").append(transactionBookings.size()).append("): [\n");

                                if (transactionBookings.isEmpty())
                                {
                                    transactionsDetails.append("        No bookings found for this transaction\n");
                                }
                                else
                                {
                                    for (var booking : transactionBookings)
                                    {
                                        transactionsDetails.append("        ").append(booking.toString().replace("\n", "\n        ")).append("\n");

                                        // Add room details
                                        Room bookingRoom = booking.getRoom();

                                        transactionsDetails.append("            Room Details: ").append(bookingRoom.getRoomNumber())
                                                .append(" (").append(bookingRoom.getType()).append(") at ")
                                                .append(bookingRoom.getHotel().getName())
                                                .append(" - $").append(bookingRoom.getPrice()).append("\n");
                                    }
                                }

                                transactionsDetails.append("    ]\n");

                                transactionsDetails.append("\n\n ==== X ====\n\n");
                            }

                            sendSuccessResponse(
                                    clientWriter,
                                    "User transactions by time",
                                    "transactions",
                                    transactionsDetails.toString()
                            );
                        }
                    }
                    catch (Exception e)
                    {
                        sendErrorResponse(clientWriter, "400", "Invalid date format. Use the format: yyyy-MM-dd'T'HH:mm");
                    }

                    return;

                case "list_all_transactions_by_time":

                    if (!user.isAdmin())
                    {
                        sendUnauthorizedResponse(clientWriter, "You are not privileged to do this operation");

                        return;
                    }

                    try
                    {
                        var timeFilteredTransactions = Database.getTransactions(
                                LocalDateTime.parse(request.get("fromTime")),
                                LocalDateTime.parse(request.get("toTime"))
                        );

                        if (timeFilteredTransactions.isEmpty())
                        {
                            sendErrorResponse(
                                    clientWriter,
                                    "404",
                                    "No transactions found in the specified time range"
                            );
                        }
                        else
                        {
                            var transactionsDetails = new StringBuilder();

                            transactionsDetails
                                    .append("===== ALL TRANSACTIONS BY TIME =====\n")
                                    .append("From: ").append(request.get("fromTime"))
                                    .append(" To: ").append(request.get("toTime"))
                                    .append("\n\n");

                            for (var transaction : timeFilteredTransactions.values())
                            {
                                // Get all bookings for this transaction
                                var transactionBookings = Database.bookings.values()
                                        .stream()
                                        .filter(booking -> booking.getTransaction().getId().equals(transaction.getId()))
                                        .toList();

                                transactionsDetails.append(transaction.toString()).append("\n");

                                transactionsDetails.append("    Bookings (").append(transactionBookings.size()).append("): [\n");

                                if (transactionBookings.isEmpty())
                                {
                                    transactionsDetails.append("        No bookings found for this transaction\n");
                                }
                                else
                                {
                                    for (var booking : transactionBookings)
                                    {
                                        transactionsDetails.append("        ").append(booking.toString().replace("\n", "\n        ")).append("\n");

                                        // Add room details
                                        Room bookingRoom = booking.getRoom();

                                        transactionsDetails.append("            Room Details: ").append(bookingRoom.getRoomNumber())
                                                .append(" (").append(bookingRoom.getType()).append(") at ")
                                                .append(bookingRoom.getHotel().getName())
                                                .append(" - $").append(bookingRoom.getPrice()).append("\n");
                                    }
                                }

                                transactionsDetails.append("    ]\n");

                                transactionsDetails.append("\n\n ==== X ====\n\n");

                            }

                            sendSuccessResponse(
                                    clientWriter,
                                    "All transactions by time",
                                    "transactions",
                                    transactionsDetails.toString()
                            );
                        }
                    }
                    catch (Exception e)
                    {
                        sendErrorResponse(clientWriter, "400", "Invalid date format. Use the format: yyyy-MM-dd'T'HH:mm");
                    }

                    return;

                case "list_all_transactions":
                    if (!user.isAdmin())
                    {
                        sendUnauthorizedResponse(clientWriter, "You are not privileged to do this operation");

                        return;
                    }

                    var allTransactions = Database.getTransactions();

                    if (allTransactions.isEmpty())
                    {
                        sendErrorResponse(
                                clientWriter,
                                "404",
                                "No transactions found"
                        );
                    }
                    else
                    {
                        var transactionsDetails = new StringBuilder();

                        transactionsDetails
                                .append("===== ALL TRANSACTIONS =====\n\n");

                        for (var transaction : allTransactions.values())
                        {
                            // Get all bookings for this transaction
                            var transactionBookings = Database.bookings.values()
                                    .stream()
                                    .filter(booking -> booking.getTransaction().getId().equals(transaction.getId()))
                                    .toList();

                            transactionsDetails.append(transaction.toString()).append("\n");

                            transactionsDetails.append("    Bookings (").append(transactionBookings.size()).append("): [\n");

                            if (transactionBookings.isEmpty())
                            {
                                transactionsDetails.append("        No bookings found for this transaction\n");
                            }
                            else
                            {
                                for (var booking : transactionBookings)
                                {
                                    transactionsDetails.append("        ").append(booking.toString().replace("\n", "\n        ")).append("\n");

                                    // Add room details
                                    Room bookingRoom = booking.getRoom();

                                    transactionsDetails.append("            Room Details: ").append(bookingRoom.getRoomNumber())
                                            .append(" (").append(bookingRoom.getType()).append(") at ")
                                            .append(bookingRoom.getHotel().getName())
                                            .append(" - $").append(bookingRoom.getPrice()).append("\n");
                                }
                            }

                            transactionsDetails.append("    ]\n");

                            transactionsDetails.append("============ XXXX ==========\n\n");
                        }

                        sendSuccessResponse(
                                clientWriter,
                                "All transactions",
                                "transactions",
                                transactionsDetails.toString()
                        );
                    }

                    return;

                default:
                    sendErrorResponse(clientWriter, "400", "Invalid request type");
            }
        }
        catch (Exception e)
        {
            System.out.println("Server error: " + e.getMessage());
        }
        finally
        {
            try {
                client.close();
            } catch (IOException e) {
                System.out.println("Error during closing client sockets");;
            }
        }
    }

    private void sendSuccessResponse(ObjectOutputStream writer, String message) throws IOException
    {
        response.put("success", "true");
        response.put("status", "200");
        response.put("message", message);
        writer.writeObject(response);
        writer.flush();
    }

    private void sendSuccessResponse(ObjectOutputStream writer, String message, String key, String value) throws IOException
    {
        response.put("success", "true");
        response.put("status", "200");
        response.put("message", message);
        response.put(key, value);
        writer.writeObject(response);
        writer.flush();
    }

    private void sendErrorResponse(ObjectOutputStream writer, String status, String message) throws IOException
    {
        response.put("success", "false");
        response.put("status", status);
        response.put("message", message);
        writer.writeObject(response);
        writer.flush();
    }

    private void sendUnauthorizedResponse(ObjectOutputStream writer, String message) throws IOException
    {
        sendErrorResponse(writer, "401", message);
    }
}
