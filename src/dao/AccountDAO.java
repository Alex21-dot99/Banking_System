package dao;

import entity.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccountDAO {

    public List<Account> findAccountsByCustomerId(String customerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String accountNumber = rs.getString("account_number");
                double balance = rs.getDouble("balance");
                String dateStr = rs.getString("date_opened");
                Date dateOpened = (dateStr != null && !dateStr.isEmpty()) ? Date.valueOf(dateStr) : null;
                String accountType = rs.getString("account_Type");

                TransactionDAO transactionDAO = new TransactionDAO();
                List<Transaction> transactions = transactionDAO.findTransactionsByAccountNumber(accountNumber);

                if ("SAVINGS".equalsIgnoreCase(accountType)) {
                    accounts.add(new SavingsAccount(accountNumber, balance, dateOpened, transactions));
                } else if ("INVESTMENT".equalsIgnoreCase(accountType)) {
                    accounts.add(new InvestmentAccount(accountNumber, balance,dateOpened, transactions));
                }else if ("CHEQUE".equalsIgnoreCase(accountType)) {
                    double overdraftLimit = rs.getDouble("overdraft_limit");

                    Employer employer = null;
                    accounts.add(new ChequeAccount(accountNumber, balance, dateOpened, transactions, overdraftLimit, employer));
                }
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    public boolean updateBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);

            return stmt.executeUpdate() > 0;
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            TransactionDAO transactionDAO = new TransactionDAO();

            while (rs.next()) {
                String accountNumber = rs.getString("account_number");
                double balance = rs.getDouble("balance");
                String dateStr = rs.getString("date_opened");
                Date dateOpened = (dateStr != null && !dateStr.isEmpty()) ? Date.valueOf(dateStr) : null;
                String accountType = rs.getString("account_type");

                List<Transaction> transactions = transactionDAO.findTransactionsByAccountNumber(accountNumber);

                if ("SAVINGS".equalsIgnoreCase(accountType)) {
                    accounts.add(new SavingsAccount(accountNumber, balance, dateOpened, transactions));
                } else if ("INVESTMENT".equalsIgnoreCase(accountType)) {
                    accounts.add(new InvestmentAccount(accountNumber, balance, dateOpened, transactions));
                } else if ("CHEQUE".equalsIgnoreCase(accountType)) {
                    double overdraftLimit = rs.getDouble("overdraft_limit");
                    Employer employer = null;
                    accounts.add(new ChequeAccount(accountNumber, balance, dateOpened, transactions, overdraftLimit, employer));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

}
