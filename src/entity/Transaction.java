package entity;

import java.util.Date;

public class Transaction
{
    private String transactionId;
    private Date timestamp;
    private double amount;
    private String transactionType;
    private String description;
    private double balanceAfter;

    public Transaction(String transactionId, Date timestamp, double amount, String transactionType, String description, double balanceAfter) {
        this.transactionId = transactionId;
        this.timestamp = new Date();
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.balanceAfter = balanceAfter;
    }

    public String getTransactionId()
    {
        return transactionId;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public double getAmount()
    {
        return amount;
    }

    public String getTransactionType()
    {
        return transactionType;
    }

    public String getDescription()
    {
        return description;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }
}

