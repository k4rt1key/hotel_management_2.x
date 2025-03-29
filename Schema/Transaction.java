package Schema;

import Helper.IDGenerator;
import java.time.LocalDateTime;

public class Transaction
{
    private final String id;

    private final User user;

    private final LocalDateTime bookedTime;

    private final PaymentType paymentMethod;

    // ------------------------------------ CONSTRUCTORS -------------------------------------------

    public Transaction( User user, LocalDateTime bookedTime, String paymentMethod )
    {
        this.id = "" + IDGenerator.generateTransactionId();

        this.user = user;

        this.bookedTime = bookedTime;

        this.paymentMethod = PaymentType.valueOf(paymentMethod);
    }


    @Override
    public String toString()
    {
        return "Transaction {\n" +
                "    ID: " + id + "\n" +
                "    User: " + user.getUsername() + " (" + user.getId() + ")\n" +
                "    Booked: " + bookedTime + "\n" +
                "    Payment: " + paymentMethod + "\n" +
                "}";
    }

    // ---------------------------------------- GETTERS ----------------------------------------------

    public String getId()
    {
        return id;
    }

    public User getUser()
    {
        return user;
    }

    public LocalDateTime getBookedTime()
    {
        return bookedTime;
    }

}
