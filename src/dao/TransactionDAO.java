package dao;

import entity.Transaction;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    public boolean saveTransaction(String accountNumber, Transaction tx) {
        String sql = "INSERT OR IGNORE INTO transactions " +
                "(transaction_id, account_number, transaction_date, amount, transaction_type, description, balance_after) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String dateStr = (tx.getTimestamp() != null)
                    ? new Date(tx.getTimestamp().getTime()).toString()
                    : new Date(System.currentTimeMillis()).toString();

            stmt.setString(1, tx.getTransactionId());
            stmt.setString(2, accountNumber);
            stmt.setString(3, dateStr);
            stmt.setDouble(4, tx.getAmount());
            stmt.setString(5, tx.getTransactionType());
            stmt.setString(6, tx.getDescription());
            stmt.setDouble(7, tx.getBalanceAfter());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Transaction> findTransactionsByAccountNumber(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? ORDER BY transaction_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String txId = rs.getString("transaction_id");
                String dateStr = rs.getString("transaction_date");
                java.util.Date txDate = (dateStr != null && !dateStr.isEmpty())
                        ? Date.valueOf(dateStr)
                        : new java.util.Date();
                double amount = rs.getDouble("amount");
                String txType = rs.getString("transaction_type");
                String description = rs.getString("description");
                double balanceAfter = rs.getDouble("balance_after");

                transactions.add(new Transaction(txId, txDate, amount, txType, description, balanceAfter));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
}