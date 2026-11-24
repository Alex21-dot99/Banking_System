package entity;

import java.util.Date;
import java.util.List;

public class SavingsAccount extends Account implements InterestBearing
{
    private static final double MONTHLY_RATE = 0.0005;
    private static final double MIN_OPENING_DEPOSIT = 100.0;

    public SavingsAccount(String accountNumber, double initialBalance, Date dateOpened, List<Transaction> transactions) {
        super(accountNumber, initialBalance, dateOpened, transactions);
    }

    @Override
    public String getAccountType() {
        return "SAVINGS";
    }

    @Override
    public double getMinimumOpeningDeposit() {
        return MIN_OPENING_DEPOSIT;
    }

    @Override
    public double CalculateInterest() {
        return getBalance() * MONTHLY_RATE;
    }

    @Override
    public Transaction payInterest() {
        double interest = CalculateInterest();
        if (interest > 0){
            deposit(interest);

            Transaction tx = new Transaction(
                    "TX-INT-" + System.currentTimeMillis(),
                    new java.util.Date(),
                    interest,
                    "INTEREST",
                    "Monthly savings interest",
                    getBalance()
            );
            addTransaction(tx);
            return tx;
        }
        return null;
    }
}


