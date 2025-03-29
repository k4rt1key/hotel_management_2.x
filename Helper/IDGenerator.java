package Helper;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator
{
    private static final AtomicInteger userId = new AtomicInteger(0);

    private static final AtomicInteger roomId = new AtomicInteger(0);

    private static final AtomicInteger bookingId = new AtomicInteger(0);

    private static final AtomicInteger hotelId = new AtomicInteger(0);

    private static final AtomicInteger transactionId = new AtomicInteger(0);

    public static int generateUserId()
    {
        return userId.incrementAndGet();
    }

    public static int generateRoomId()
    {
        return roomId.incrementAndGet();
    }

    public static int generateBookingId()
    {
        return bookingId.incrementAndGet();
    }

    public static int generateHotelId()
    {
        return hotelId.incrementAndGet();
    }

    public static int generateTransactionId()
    {
        return transactionId.incrementAndGet();
    }
}
