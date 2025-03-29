package Schema;

import Helper.IDGenerator;

import java.time.LocalDateTime;

public class Booking
{

    private final String id;

    private final User guest;

    private final Room room;

    private final LocalDateTime checkInTime;

    private final LocalDateTime checkOutTime;

    private final Transaction transaction;

    // ------------------------------------ CONSTRUCTORS -------------------------------------------

    public Booking( User user, Room room, String checkInTime, String checkOutTime, Transaction transaction )
    {
        this.id = "" + IDGenerator.generateBookingId();

        this.room = room;

        this.guest = user;

        this.transaction = transaction;

        this.checkInTime = LocalDateTime.parse(checkInTime);

        this.checkOutTime = LocalDateTime.parse(checkOutTime);
    }

    @Override
    public String toString()
    {
        return "Booking {\n" +
                "    ID: " + id + "\n" +
                "    Guest: " + guest.getUsername() + " (" + guest.getId() + ")\n" +
                "    Room: " + room.getRoomNumber() + " at " + room.getHotel().getName() + "\n" +
                "    Check-In: " + checkInTime + "\n" +
                "    Check-Out: " + checkOutTime + "\n" +
                "    Transaction ID: " + transaction.getId() + "\n" +
                "}";
    }

    // -------------------------------------------- GETTERS ---------------------------------------------

    public String getId()
    {
        return id;
    }

    public Room getRoom()
    {
        return room;
    }

    public LocalDateTime getCheckInTime()
    {
        return checkInTime;
    }

    public LocalDateTime getCheckOutTime()
    {
        return checkOutTime;
    }

    public Transaction getTransaction()
    {
        return transaction;
    }
}

