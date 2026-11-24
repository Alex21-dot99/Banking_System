package entity;

import java.util.ArrayList;
import java.util.List;

public abstract class Customer extends User
{
    private String address;
    private String phone;
    private String email;
    private List<Account>accounts;

    public Customer(String userId, String firstName, String surname, String username, String passwordHash, String address, String phone, String email) {
        super(userId, firstName, surname, username, passwordHash);
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.accounts = new ArrayList<>();
    }

    @Override
    public String getRole()
    {
        return "Customer";
    }

    public abstract String getCustomerType();

    public void addAccount(Account account)
    {
        this.accounts.add(account);
    }

    public List<Account> getAccounts()
    {
        return accounts;
    }

    public String getAddress()
    {
        return address;
    }

    public String getPhone()
    {
        return phone;
    }

    public String getEmail()
    {
        return email;
    }
}
