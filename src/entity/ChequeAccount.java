package entity;

import java.util.Date;
import java.util.List;

public class ChequeAccount extends Account implements Withdrawable
{
 private double overdraftLimit;
 private Employer employer;

    public ChequeAccount(String accountNumber, double initialBalance, Date dateOpened, List<Transaction> transactions, double overdraftlimit, Employer employer) {
        super(accountNumber, initialBalance, dateOpened, transactions);
        this.overdraftLimit = overdraftlimit;
        this.employer = employer;
    }

    @Override
    public String getAccountType() {
        return "CHEQUE";
    }

    @Override
    public double getMinimumOpeningDeposit() {
        return 50.0;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }

        if (amount > (getBalance() + overdraftLimit)){
            return false;
        }
        setBalance(getBalance() - amount);

        Transaction tx = new Transaction(
                "TX-WDR-" + System.currentTimeMillis(),
                new java.util.Date(),
                amount,
                "WITHDRAWAL",
                "Cheque account withdrawal",
                getBalance()
        );
        addTransaction(tx);
        return true;
    }

    public Employer getEmployer() {
        return employer;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }
}