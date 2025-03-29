package Database;

import Schema.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Database {
    private Database() {
    }

    public static final HashMap<String, User> users = new HashMap<>();

    public static final HashMap<String, Hotel> hotels = new HashMap<>();

    public static final HashMap<String, Room> rooms = new HashMap<>();

    public static final HashMap<String, Transaction> transactions = new HashMap<>();

    public static final ConcurrentHashMap<String, Booking> bookings = new ConcurrentHashMap<>();

    public static void populateSeedData() {

        // -------------------------- Users --------------------------------

        User admin = new User("admin", "admin");

        User user = new User("user", "user");

        users.put(admin.getUsername(), admin);

        users.put(user.getUsername(), user);

        // -------------------------- Hotels -----------------------------------

        var hotel1 = new Hotel("Taj Hotel - Mumbai");

        var hotel2 = new Hotel("Taj Hotel - Udaipur");

        var hotel3 = new Hotel("Taj Hotel - Ahmedabad");

        hotels.put(hotel1.getId(), hotel1);

        hotels.put(hotel2.getId(), hotel2);

        hotels.put(hotel3.getName(), hotel3);

        // ==================== Rooms ====================

        Room room1 = new Room("A101", 100, RoomType.SINGLE, hotel1);

        Room room2 = new Room("A102", 100, RoomType.SINGLE, hotel1);

        Room room3 = new Room("B101", 150, RoomType.DOUBLE, hotel1);

        Room room4 = new Room("B102", 150, RoomType.DOUBLE, hotel1);

        Room room5 = new Room("C101", 250, RoomType.DELUX, hotel1);

        Room room6 = new Room("D101", 350, RoomType.SUITE, hotel1);

        rooms.put(room1.getId(), room1);

        rooms.put(room2.getId(), room2);

        rooms.put(room3.getId(), room3);

        rooms.put(room4.getId(), room4);

        rooms.put(room5.getId(), room5);

        rooms.put(room6.getId(), room6);

        Room room7 = new Room("A201", 120, RoomType.SINGLE, hotel2);

        Room room8 = new Room("A202", 120, RoomType.SINGLE, hotel2);

        Room room9 = new Room("A202", 120, RoomType.SINGLE, hotel2);

        Room room10 = new Room("B201", 170, RoomType.DOUBLE, hotel2);

        Room room11 = new Room("B202", 170, RoomType.DOUBLE, hotel2);

        Room room12 = new Room("C201", 270, RoomType.DELUX, hotel2);

        Room room13 = new Room("C202", 270, RoomType.DELUX, hotel2);

        Room room14 = new Room("D201", 370, RoomType.SUITE, hotel2);

        rooms.put(room7.getId(), room7);

        rooms.put(room8.getId(), room8);

        rooms.put(room9.getId(), room9);

        rooms.put(room10.getId(), room10);

        rooms.put(room11.getId(), room11);

        rooms.put(room12.getId(), room12);

        rooms.put(room13.getId(), room13);

        rooms.put(room14.getId(), room14);

        Room room15 = new Room("A301", 90, RoomType.SINGLE, hotel3);

        Room room16 = new Room("A302", 90, RoomType.SINGLE, hotel3);

        Room room17 = new Room("B301", 140, RoomType.DOUBLE, hotel3);

        Room room18 = new Room("B302", 140, RoomType.DOUBLE, hotel3);

        Room room19 = new Room("C301", 240, RoomType.DELUX, hotel3);

        Room room20 = new Room("D301", 340, RoomType.SUITE, hotel3);

        rooms.put(room15.getId(), room15);

        rooms.put(room16.getId(), room16);

        rooms.put(room17.getId(), room17);

        rooms.put(room18.getId(), room18);

        rooms.put(room19.getId(), room19);

        rooms.put(room20.getId(), room20);

        System.out.println("Populated seed data successfully");

    }

    // -------------------------------------- CRUD ----------------------------------------

    // ----------- USER ----------

    public static User createUser(String username, String password)
    {
        return new User(username, password);
    }

    public static User getUser(String username)
    {
        return users.get(username);
    }

    public static User changePassword(String username, String password, String newPassword)
    {
        var user = users.get(username);

        if (user == null || !user.validatePassword(password))
        {
            return null;
        }

        user.setPassword(newPassword);

        return user;
    }

    public static User removeUser(String username)
    {
        return users.remove(username);
    }

    // ----------- HOTEL ----------

    public static Hotel createHotel(String name)
    {
        return new Hotel(name);
    }

    public static Hotel updateHotel(String id, String name)
    {
        var hotel = hotels.get(id);

        hotel.setName(name);

        return hotel;
    }

    public static Hotel removeHotel(String id)
    {
        return hotels.remove(id);
    }

    // ----------- ROOM ----------

    public static Room createRoom(String roomNumber, int price, String type, Hotel hotel)
    {
        return new Room(roomNumber, price, type, hotel);
    }

    public static Room getRoom(String id)
    {
        return rooms.get(id);
    }

    public static Room updateRoom(String id, String roomNumber, Integer price)
    {
        var room = rooms.get(id);

        if (room == null)
        {
            return null;
        }

        if (roomNumber != null)
        {
            room.setRoomNumber(roomNumber);
        }

        if (price != null)
        {
            room.setPrice(price);
        }

        return room;
    }

    // ----------- TRANSACTION ----------


    public static Transaction createTransaction(User user, LocalDateTime bookingTime, String paymentMethod)
    {
        Transaction transaction = new Transaction(user, bookingTime, paymentMethod);

        transactions.put(transaction.getId(), transaction);

        return transaction;
    }

    public static HashMap<String, Transaction> getTransactions()
    {
        return transactions;
    }

    public static HashMap<String, Transaction> getTransactions(User user)
    {

        return (HashMap<String, Transaction>) transactions

                .values()

                .stream()

                .filter((transaction) ->  transaction.getUser().getId().equals(user.getId()))

                .collect(Collectors.toMap(Transaction::getId, transaction -> transaction));
    }

    public static HashMap<String, Transaction> getTransactions(User user, LocalDateTime fromTime, LocalDateTime toTime)
    {
        return (HashMap<String, Transaction>) transactions

                .values()

                .stream()

                .filter((transaction) -> transaction.getUser().getId().equals(user.getId()))

                .filter((transaction -> transaction.getBookedTime().isAfter(fromTime) && transaction.getBookedTime().isBefore(toTime)))

                .collect(Collectors.toMap(Transaction::getId, transaction -> transaction));
    }

    public static HashMap<String, Transaction> getTransactions(LocalDateTime fromTime, LocalDateTime toTime)
    {
        System.out.println(fromTime + "-" + toTime);
        return (HashMap<String, Transaction>) transactions

                .values()

                .stream()

                .filter((transaction -> transaction.getBookedTime().isAfter(fromTime) && transaction.getBookedTime().isBefore(toTime)))

                .collect(Collectors.toMap(Transaction::getId, transaction -> transaction));
    }

    // ----------- BOOKING ----------

    public static Booking createBooking(User user, Room room, String checkInTime, String checkOutTime, Transaction transaction)
    {
        return new Booking(user, room, checkInTime, checkOutTime, transaction);
    }

    public static HashMap<String, Booking> bookRooms(User user, List<Room> rooms, String checkInTime, String checkOutTime, String paymentMethod) throws Exception
    {
        var transaction = createTransaction(user, LocalDateTime.now(), paymentMethod);

        var lockedRooms = new ArrayList<Room>();

        var doneBookings = new HashMap<String, Booking>();

        try
        {
            // Lock all rooms
            for (var room : rooms)
            {
                if (!room.getLock().tryLock(1, TimeUnit.SECONDS))
                {
                    throw new Exception("Could not lock room: " + room.getId());
                }

                lockedRooms.add(room);
            }

            // Check availability
            for (var room : rooms)
            {
                if (!checkAvailability(room, checkInTime, checkOutTime))
                {
                    throw new Exception("Room not available: " + room.getId());
                }
            }

            // Create bookings
            for (var room : rooms)
            {
                var booking = createBooking(user, room, checkInTime, checkOutTime, transaction);

                doneBookings.put(booking.getId(), booking);

                bookings.put(booking.getId(), booking);
            }

            return doneBookings;

        }
        catch (Exception e)
        {
            rollBack(lockedRooms, doneBookings);
            throw e;
        }
        finally
        {
            unlockRooms(lockedRooms);
        }
    }

    private static void rollBack(ArrayList<Room> lockedRooms, HashMap<String,Booking> doneBookings)
    {

        for (var doneBooking : doneBookings.values())
        {
            bookings.remove(doneBooking.getId());
        }

        unlockRooms(lockedRooms);
    }

    private static void unlockRooms(ArrayList<Room> lockedRooms)
    {
        for (var lockedRoom : lockedRooms)
        {
            try
            {
                lockedRoom.getLock().unlock();
            }
            catch (Exception e)
            {
                return;
            }
        }

    }

    public static boolean checkAvailability(Room room, String checkInTimeStr, String checkOutTimeStr)
    {
        LocalDateTime checkInTime = LocalDateTime.parse(checkInTimeStr);

        LocalDateTime checkOutTime = LocalDateTime.parse(checkOutTimeStr);

        return bookings
                .values()

                .stream()

                .filter(booking -> booking.getRoom().getId().equals(room.getId()))

                .noneMatch(booking ->
                        (checkInTime.isBefore(booking.getCheckOutTime()) && checkOutTime.isAfter(booking.getCheckInTime()))
                );
    }


    public static HashMap<String, Room> getAvailableRooms(LocalDateTime checkInTime, LocalDateTime checkOutTime)
    {
        return (HashMap<String, Room>) rooms.values()

                .stream()

                .filter(room -> bookings.values().stream()

                        .filter(booking -> booking.getRoom().getId().equals(room.getId()))

                        .noneMatch(booking ->
                                checkInTime.isBefore(booking.getCheckOutTime()) && checkOutTime.isAfter(booking.getCheckInTime())
                        )
                )

                .collect(Collectors.toMap(Room::getId, room -> room));
    }
}
