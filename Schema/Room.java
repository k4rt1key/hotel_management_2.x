package Schema;


import Helper.IDGenerator;

import java.util.concurrent.locks.ReentrantLock;

public class Room
{

    private final String id;

    private String roomNumber;

    private int price;

    private final RoomType type;

    private final Hotel hotel;

    private final ReentrantLock lock;

    // ------------------------------------ CONSTRUCTORS -------------------------------------------

    public Room ( String roomNumber, int price, RoomType type, Hotel hotel )
    {
        this.id = "" + IDGenerator.generateRoomId();

        this.roomNumber = roomNumber;

        this.price = price;

        this.type = type;

        this.hotel = hotel;

        this.lock = new ReentrantLock();
    }

    public Room ( String roomNumber, int price, String type, Hotel hotel )
    {
        this.id = "" + IDGenerator.generateRoomId();

        this.roomNumber = roomNumber;

        this.price = price;

        this.type = RoomType.valueOf(type);

        this.hotel = hotel;

        this.lock = new ReentrantLock();
    }

    @Override
    public String toString()
    {
        return "Room {\n" +
                "    ID: " + id + "\n" +
                "    Number: " + roomNumber + "\n" +
                "    Price: $" + price + "\n" +
                "    Type: " + type + "\n" +
                "    Hotel: " + hotel.getName() + " (" + hotel.getId() + ")\n" +
                "}";
    }

    // ------------------------------------- GETTERS --------------------------------------------

    public String getId()
    {
        return id;
    }

    public String getRoomNumber()
    {
        return roomNumber;
    }

    public int getPrice()
    {
        return price;
    }

    public RoomType getType()
    {
        return type;
    }

    public Hotel getHotel()
    {
        return hotel;
    }

    public ReentrantLock getLock()
    {
        return lock;
    }

    // ------------------------------------- SETTERS --------------------------------------------

    public void setRoomNumber( String roomNumber )
    {
        this.roomNumber = roomNumber;
    }

    public void setPrice( int price )
    {
        this.price = price;
    }

}
