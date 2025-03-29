package Schema;

import Helper.IDGenerator;

public class User
{
    private final String id;

    private final String username;

    private String password;

    private final boolean isAdmin;

    // ------------------------------------ CONSTRUCTOR -------------------------------------------

    public User(String username, String password)
    {
        this.id = "" + IDGenerator.generateUserId();

        this.username = username;

        this.password = password;

       this.isAdmin = this.username.equals("admin") && this.password.equals("admin");
    }

    @Override
    public String toString()
    {
        return "User {\n" +
                "    ID: " + id + "\n" +
                "    Username: " + username + "\n" +
                "    Role: " + (isAdmin ? "Admin" : "Guest") + "\n" +
                "}";
    }

    // -------------------------------------GETTERS-------------------------------------------

    public String getId()
    {
        return id;
    }

    public String getUsername()
    {
        return username;
    }

    public boolean isAdmin()
    {
        return isAdmin;
    }

    // -------------------------------------SETTERS--------------------------------------------

    public void setPassword(String newPassword)
    {
        this.password = newPassword;
    }

    // -------------------------------------HELPERS----------------------------------------------

    public boolean validatePassword(String inputPassword)
    {
        return password.equals(inputPassword);
    }

}
