package control;

import dao.AccountDAO;
import dao.TransactionDAO;
import entity.Account;
import entity.InterestBearing;
import entity.Transaction;
import entity.Withdrawable;

import java.util.List;

public class AccountController {
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;

    public AccountController() {
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
    }

    public boolean deposit(Account account, double amount) {
        if (amount <= 0) {
            return false;
        }
        account.deposit(amount);
        boolean updated = accountDAO.updateBalance(account.getAccountNumber(), account.getBalance());
        if (updated) {
            saveLatestTransaction(account);
        }
        return updated;
    }

    public boolean withdraw(Account account, double amount) {
        if (amount <= 0) {
            return false;
        }

        if (account instanceof Withdrawable) {
            boolean success = ((Withdrawable) account).withdraw(amount);
            if (success) {
                boolean updated = accountDAO.updateBalance(account.getAccountNumber(), account.getBalance());
                if (updated) {
                    saveLatestTransaction(account);
                }
                return updated;
            }
        }
        return false;
    }

    public boolean processInterest(Account account) {
        if (account instanceof InterestBearing) {
            Transaction tx = ((InterestBearing) account).payInterest();
            if (tx != null) {
                boolean updated = accountDAO.updateBalance(account.getAccountNumber(), account.getBalance());
                if (updated) {
                    transactionDAO.saveTransaction(account.getAccountNumber(), tx);
                }
                return updated;
            }
        }
        return false;
    }

    private void saveLatestTransaction(Account account) {
        List<Transaction> txList = account.getTransactions();
        if (txList != null && !txList.isEmpty()) {
            Transaction latestTx = txList.get(txList.size() - 1);
            transactionDAO.saveTransaction(account.getAccountNumber(), latestTx);
        }
    }
}