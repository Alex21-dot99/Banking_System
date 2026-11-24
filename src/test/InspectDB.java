package test;

import dao.DatabaseConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class InspectDB {
    public static void main(String[] args) throws Exception {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            System.out.println("--- CUSTOMERS ---");
            ResultSet rsCust = stmt.executeQuery("SELECT * FROM customers");
            int custCols = rsCust.getMetaData().getColumnCount();
            while (rsCust.next()) {
                for (int i = 1; i <= custCols; i++) {
                    System.out.print(rsCust.getMetaData().getColumnName(i) + "=" + rsCust.getString(i) + (i < custCols ? " | " : ""));
                }
                System.out.println();
            }

            System.out.println("--- STAFF ---");
            ResultSet rsStaff = stmt.executeQuery("SELECT * FROM staff");
            int staffCols = rsStaff.getMetaData().getColumnCount();
            while (rsStaff.next()) {
                for (int i = 1; i <= staffCols; i++) {
                    System.out.print(rsStaff.getMetaData().getColumnName(i) + "=" + rsStaff.getString(i) + (i < staffCols ? " | " : ""));
                }
                System.out.println();
            }

            System.out.println("--- ACCOUNTS ---");
            ResultSet rsAcc = stmt.executeQuery("SELECT * FROM accounts");
            int accCols = rsAcc.getMetaData().getColumnCount();
            while (rsAcc.next()) {
                for (int i = 1; i <= accCols; i++) {
                    System.out.print(rsAcc.getMetaData().getColumnName(i) + "=" + rsAcc.getString(i) + (i < accCols ? " | " : ""));
                }
                System.out.println();
            }
        }
    }
}