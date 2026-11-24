package entity;

import java.util.Date;
import java.util.List;

public class InvestmentAccount extends Account implements InterestBearing, Withdrawable{
    private static final double MONTHLY_RATE = 0.05;
    private static final double MIN_OPENING_DEPOSIT = 500.00;

    public InvestmentAccount(String accountNumber, double initialBalance, Date dateOpened, List<Transaction> transactions) {
        super(accountNumber, initialBalance, dateOpened, transactions);
    }

    @Override
    public String getAccountType() {
        return "INVESTMENT";
    }

    @Override
    public double getMinimumOpeningDeposit() {
        return MIN_OPENING_DEPOSIT;
    }

    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0 || amount > getBalance()) {
            return false;
        }
        setBalance(getBalance() - amount);
        return true;
    }

    @Override
    public double CalculateInterest() {
        return getBalance() * MONTHLY_RATE;
    }

    @Override
    public Transaction payInterest() {
        double interest = CalculateInterest();
        if (interest > 0) {
            deposit(interest);
            Transaction tx = new Transaction(
                    "TX-INT-" + System.currentTimeMillis(),
                    new java.util.Date(),
                    interest,
                    "INTEREST",
                    "Monthly investment interest",
                    getBalance()
            );
            addTransaction(tx);
            return tx;
        }
        return null;
    }
}
