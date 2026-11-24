package control;

import dao.CustomerDAO;
import dao.DatabaseConnection;
import entity.BankAdministrator;
import entity.BankManager;
import entity.BankStaff;
import entity.Customer;
import entity.LoanOfficer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    private final CustomerDAO customerDAO;

    public static class LoginResult {
        private final String role;
        private final Customer customer;
        private final BankStaff staff;

        public LoginResult(String role, Customer customer, BankStaff staff) {
            this.role = role;
            this.customer = customer;
            this.staff = staff;
        }

        public String getRole() {
            return role;
        }

        public Customer getCustomer() {
            return customer;
        }

        public BankStaff getStaff() {
            return staff;
        }
    }

    public LoginController() {
        this.customerDAO = new CustomerDAO();
    }

    public Customer authenticateCustomer(String email, String password) {
        Customer customer = customerDAO.findByEmail(email);

        if (customer != null) {
            if (customer.getPasswordHash() != null && customer.getPasswordHash().equals(password)) {
                return customer;
            }
        }
        return null;
    }

    public BankStaff authenticateStaff(String role, String email, String password) {
        String sql = "SELECT staff_id, name, email, role FROM staff " +
                "WHERE email = ? AND password_hash = ? AND role = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, role);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String staffId = rs.getString("staff_id");
                    String name = rs.getString("name");
                    String dbEmail = rs.getString("email");
                    String dbRole = rs.getString("role");

                    return switch (dbRole) {
                        case "Bank Administrator" -> new BankAdministrator(staffId, name, dbEmail);
                        case "Loan Officer" -> new LoanOfficer(staffId, name, dbEmail);
                        case "Bank Manager" -> new BankManager(staffId, name, dbEmail);
                        default -> null;
                    };
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public LoginResult authenticate(String role, String email, String password) {
        if (role == null || email == null || password == null) {
            return null;
        }

        if ("Customer".equals(role)) {
            Customer customer = authenticateCustomer(email, password);
            return (customer != null) ? new LoginResult(role, customer, null) : null;
        }

        BankStaff staff = authenticateStaff(role, email, password);
        return (staff != null) ? new LoginResult(role, null, staff) : null;
    }
}