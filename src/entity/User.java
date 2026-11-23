package entity;

public abstract class User
{
    private String userId;
    private String firstName;
    private String surname;
    private String username;
    private String passwordHash;

    public User(String userId, String firstName, String surname, String username, String passwordHash)
    {
        this.userId = userId;
        this.firstName = firstName;
        this.surname = surname;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public abstract String getRole();

    public boolean authenticate(String inputPassword){
        return this.passwordHash.equals(inputPassword);
    }

    public String getUserId()
    {
        return userId;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getSurname()
    {
        return surname;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }
}
