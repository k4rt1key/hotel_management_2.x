package Schema;

import Helper.IDGenerator;
import java.util.List;
import java.util.stream.Collectors;
import Database.*;

public class Hotel
{
    private final String id;

    private String name;

    // ------------------------------------- CONSTRUCTOR -------------------------------------------

    public Hotel(String name)
    {
        this.id = "" + IDGenerator.generateHotelId();

        this.name = name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hotel {\n");
        sb.append("    ID: ").append(id).append("\n");
        sb.append("    Name: ").append(name).append("\n");

        // Get all rooms for this hotel
        List<Room> hotelRooms = Database.rooms.values().stream()
                .filter(room -> room.getHotel().getId().equals(this.id))
                .toList();

        sb.append("    Rooms (").append(hotelRooms.size()).append("): [\n");
        if (hotelRooms.isEmpty()) {
            sb.append("        No rooms available\n");
        } else {
            for (Room room : hotelRooms) {
                sb.append("        ").append(room.getId()).append(": ");
                sb.append(room.getRoomNumber()).append(" (");
                sb.append(room.getType()).append(") - $");
                sb.append(room.getPrice()).append("\n");
            }
        }
        sb.append("    ]\n");
        sb.append("}");

        return sb.toString();
    }

    // -------------------------------------------- GETTERS ---------------------------------------------


    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    // -------------------------------------------- SETTERS ---------------------------------------------

    public void setName(String name)
    {
        this.name = name;
    }

}
