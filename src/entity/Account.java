package entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Account
{
    private String accountNumber;
    private double balance;
    private Date dateOpened;
    private List<Transaction> transactions;

    public Account(String accountNumber, double initialBalance, Date dateOpened, List<Transaction> transactions) {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
        this.dateOpened = (dateOpened != null) ? dateOpened : new Date();
        this.transactions = (transactions != null) ? transactions : new ArrayList<>();
    }

    public abstract String getAccountType();
    public abstract double getMinimumOpeningDeposit();

    public boolean deposit(double amount) {
        return deposit(amount, "Account deposit");
    }

    public boolean deposit(double amount, String description) {
        if (amount <= 0) {
            return false;
        }
        this.balance += amount;

        Transaction tx = new Transaction(
                "TX-DEP-" + System.currentTimeMillis(),
                new Date(),
                amount,
                "DEPOSIT",
                description,
                this.balance
        );
        addTransaction(tx);
        return true;
    }

    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }
        this.balance -= amount;
        return true;
    }
    public void addTransaction(Transaction transaction)
    {
        this.transactions.add(transaction);
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }

    public double getBalance()
    {
        return balance;
    }

    public Date getDateOpened()
    {
        return dateOpened;
    }

    public List<Transaction> getTransactions()
    {
        return transactions;
    }

    protected  void setBalance(double balance)
    {
        this.balance = balance;
    }
}
