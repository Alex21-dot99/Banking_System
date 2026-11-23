package test;

import control.LoginController;
import dao.AccountDAO;
import dao.CustomerDAO;
import dao.DatabaseConnection;
import entity.Account;
import entity.BankAdministrator;
import entity.BankManager;
import entity.BankStaff;
import entity.Customer;
import entity.LoanOfficer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class IntegrationTestRunner {

    private static final String CUSTOMER_EMAIL = "alex@gmail.com";
    private static final String CUSTOMER_PASSWORD = "kingswitch";

    public static void main(String[] args) {
        System.out.println("=== Starting Banking System Integration Tests ===");

        testDatabaseConnection();
        testCustomerLoginFlow();
        testStaffRoleRouting("Bank Administrator");
        testStaffRoleRouting("Loan Officer");
        testStaffRoleRouting("Bank Manager");
        testInvalidCredentialsRejected();
        testAccountPersistenceFlow();

        System.out.println("=== All Integration Tests Passed ===");
    }

    private static void testDatabaseConnection() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null || conn.isClosed()) {
                throw new AssertionError("IT-00 Failed: Database connection is null or closed.");
            }
            System.out.println("[PASS] IT-00: DatabaseConnection.getConnection() connected to SQLite.");
        } catch (Exception e) {
            throw new AssertionError("IT-00 Failed: " + e.getMessage(), e);
        }
    }

    private static void testCustomerLoginFlow() {
        LoginController controller = new LoginController();
        CustomerDAO customerDAO = new CustomerDAO();

        Customer directLookup = customerDAO.findByEmail(CUSTOMER_EMAIL);
        if (directLookup == null) {
            throw new AssertionError("IT-01 Failed: CustomerDAO.findByEmail returned null for " + CUSTOMER_EMAIL);
        }

        LoginController.LoginResult result = controller.authenticate("Customer", CUSTOMER_EMAIL, CUSTOMER_PASSWORD);
        if (result == null || !"Customer".equals(result.getRole()) || result.getCustomer() == null) {
            throw new AssertionError("IT-01 Failed: LoginController.authenticate did not return a Customer for " + CUSTOMER_EMAIL);
        }

        System.out.println("[PASS] IT-01: Customer authenticated -> " + result.getCustomer().getEmail());
    }

    private static void testStaffRoleRouting(String expectedRole) {
        String[] creds = findFirstStaffCredentials(expectedRole);
        if (creds == null) {
            throw new AssertionError("IT-02 Failed: No row in `staff` table with role [" + expectedRole + "].");
        }

        String email = creds[0];
        String password = creds[1];

        LoginController controller = new LoginController();
        LoginController.LoginResult result = controller.authenticate(expectedRole, email, password);

        if (result == null || !expectedRole.equals(result.getRole()) || result.getStaff() == null) {
            throw new AssertionError("IT-02 Failed for role [" + expectedRole + "] (" + email + "): authentication returned null.");
        }

        BankStaff staff = result.getStaff();
        boolean correctSubclass = switch (expectedRole) {
            case "Bank Administrator" -> staff instanceof BankAdministrator;
            case "Loan Officer" -> staff instanceof LoanOfficer;
            case "Bank Manager" -> staff instanceof BankManager;
            default -> false;
        };

        if (!correctSubclass) {
            throw new AssertionError("IT-02 Failed: Expected subclass for " + expectedRole
                    + ", got " + staff.getClass().getSimpleName());
        }

        System.out.println("[PASS] IT-02 (" + expectedRole + "): Returned "
                + staff.getClass().getSimpleName() + " for " + staff.getEmail());
    }

    private static void testInvalidCredentialsRejected() {
        String[] adminCreds = findFirstStaffCredentials("Bank Administrator");
        String targetEmail = (adminCreds != null) ? adminCreds[0] : "nonexistent@bank.bw";

        LoginController controller = new LoginController();

        LoginController.LoginResult wrongPass = controller.authenticate("Bank Administrator", targetEmail, "wrong_password_check");
        if (wrongPass != null) {
            throw new AssertionError("IT-03 Failed: Wrong password should return null.");
        }

        LoginController.LoginResult wrongRole = controller.authenticate("Loan Officer", targetEmail, "wrong_password_check");
        if (wrongRole != null) {
            throw new AssertionError("IT-03 Failed: Mismatched role/password should return null.");
        }

        System.out.println("[PASS] IT-03: Invalid credentials and mismatched role rejected with null.");
    }

    private static void testAccountPersistenceFlow() {
        AccountDAO accountDAO = new AccountDAO();
        List<Account> allAccounts = accountDAO.findAll();

        if (allAccounts == null || allAccounts.isEmpty()) {
            throw new AssertionError("IT-04 Failed: No accounts found in SQLite via findAll().");
        }

        String customerId = findFirstAccountCustomerId();
        if (customerId == null) {
            throw new AssertionError("IT-04 Failed: Could not read customer_id from accounts table.");
        }

        List<Account> customerAccounts = accountDAO.findAccountsByCustomerId(customerId);
        if (customerAccounts == null || customerAccounts.isEmpty()) {
            throw new AssertionError("IT-04 Failed: findAccountsByCustomerId returned empty for " + customerId);
        }

        Account targetAccount = customerAccounts.get(0);
        String accountNumber = targetAccount.getAccountNumber();
        double originalBalance = targetAccount.getBalance();
        double updatedBalance = originalBalance + 50.0;

        if (!accountDAO.updateBalance(accountNumber, updatedBalance)) {
            throw new AssertionError("IT-04 Failed: updateBalance returned false for account " + accountNumber);
        }

        List<Account> reloaded = accountDAO.findAccountsByCustomerId(customerId);
        double persistedBalance = reloaded.get(0).getBalance();

        if (Math.abs(persistedBalance - updatedBalance) > 0.001) {
            throw new AssertionError("IT-04 Failed: Expected balance " + updatedBalance
                    + " in SQLite, but found " + persistedBalance);
        }

        // Restore original balance so database stays clean for presentation
        accountDAO.updateBalance(accountNumber, originalBalance);

        System.out.println("[PASS] IT-04: Balance updated (" + originalBalance
                + " -> " + persistedBalance + ") and restored in SQLite for " + accountNumber);
    }

    private static String[] findFirstStaffCredentials(String role) {
        String sql = "SELECT email, password_hash FROM staff WHERE role = ? LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, role);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new String[]{rs.getString("email"), rs.getString("password_hash")};
                }
            }
        } catch (Exception e) {
            throw new AssertionError("Failed querying staff table for role [" + role + "]: " + e.getMessage(), e);
        }
        return null;
    }

    private static String findFirstAccountCustomerId() {
        String sql = "SELECT customer_id FROM accounts LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getString("customer_id");
            }
        } catch (Exception e) {
            throw new AssertionError("Failed querying accounts table: " + e.getMessage(), e);
        }
        return null;
    }
}