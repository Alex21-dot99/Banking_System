package test;

import control.LoginController;
import dao.AccountDAO;
import entity.Account;
import entity.BankStaff;
import entity.Customer;

import java.util.List;
import java.util.Scanner;

public class ConsoleBankingApp {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LoginController loginController = new LoginController();
        AccountDAO accountDAO = new AccountDAO();

        System.out.println("=========================================");
        System.out.println("   BANKING SYSTEM - CONSOLE APPLICATION   ");
        System.out.println("=========================================");
        System.out.println("Select Role:");
        System.out.println("1. Customer");
        System.out.println("2. Bank Administrator");
        System.out.println("3. Loan Officer");
        System.out.println("4. Bank Manager");
        System.out.print("Choice (1-4): ");

        String choice = scanner.nextLine().trim();
        String role = switch (choice) {
            case "1" -> "Customer";
            case "2" -> "Bank Administrator";
            case "3" -> "Loan Officer";
            case "4" -> "Bank Manager";
            default -> null;
        };

        if (role == null) {
            System.out.println("Invalid role selection. Exiting.");
            scanner.close();
            return;
        }

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        LoginController.LoginResult result = loginController.authenticate(role, email, password);
        if (result == null) {
            System.out.println("Login failed: Invalid role, email, or password.");
            scanner.close();
            return;
        }

        if ("Customer".equals(result.getRole()) && result.getCustomer() != null) {
            runCustomerMenu(scanner, accountDAO, result.getCustomer());
        } else if (result.getStaff() != null) {
            runStaffMenu(scanner, accountDAO, result.getRole(), result.getStaff());
        } else {
            System.out.println("Authentication returned an incomplete session.");
        }

        scanner.close();
    }

    private static void runCustomerMenu(Scanner scanner, AccountDAO accountDAO, Customer customer) {
        String customerId = customer.getUserId();
        System.out.println("\nWelcome, " + customer.getEmail() + " (Role: Customer)");
        boolean running = true;

        while (running) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. View My Accounts");
            System.out.println("2. Deposit Funds");
            System.out.println("3. Withdraw Funds");
            System.out.println("4. Transfer Funds");
            System.out.println("5. Exit");
            System.out.print("Select option: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> displayCustomerAccounts(accountDAO, customerId);
                case "2" -> handleDeposit(scanner, accountDAO, customerId);
                case "3" -> handleWithdraw(scanner, accountDAO, customerId);
                case "4" -> handleTransfer(scanner, accountDAO, customerId);
                case "5" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        System.out.println("Logged out. Goodbye!");
    }

    private static void displayCustomerAccounts(AccountDAO accountDAO, String customerId) {
        List<Account> accounts = accountDAO.findAccountsByCustomerId(customerId);
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts found for customer ID: " + customerId);
            return;
        }
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.println((i + 1) + ". Account: " + acc.getAccountNumber()
                    + " | Type: " + acc.getClass().getSimpleName()
                    + " | Balance: " + acc.getBalance());
        }
    }

    private static Account pickCustomerAccount(Scanner scanner, AccountDAO accountDAO, String customerId, String prompt) {
        List<Account> accounts = accountDAO.findAccountsByCustomerId(customerId);
        if (accounts == null || accounts.isEmpty()) {
            System.out.println("No accounts found for customer ID: " + customerId);
            return null;
        }
        for (int i = 0; i < accounts.size(); i++) {
            Account acc = accounts.get(i);
            System.out.println((i + 1) + ". " + acc.getAccountNumber()
                    + " (" + acc.getClass().getSimpleName() + ") - Balance: " + acc.getBalance());
        }
        System.out.print(prompt + " (1-" + accounts.size() + "): ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= accounts.size()) {
                System.out.println("Invalid account selection.");
                return null;
            }
            return accounts.get(idx);
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number.");
            return null;
        }
    }

    private static double readPositiveAmount(Scanner scanner, String prompt) {
        System.out.print(prompt);
        try {
            double amount = Double.parseDouble(scanner.nextLine().trim());
            if (amount <= 0) {
                System.out.println("Amount must be greater than 0.");
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount format.");
            return -1;
        }
    }

    private static void handleDeposit(Scanner scanner, AccountDAO accountDAO, String customerId) {
        Account target = pickCustomerAccount(scanner, accountDAO, customerId, "Select account to deposit into");
        if (target == null) return;

        double amount = readPositiveAmount(scanner, "Enter deposit amount: ");
        if (amount <= 0) return;

        double newBalance = target.getBalance() + amount;
        if (accountDAO.updateBalance(target.getAccountNumber(), newBalance)) {
            System.out.println("Deposit successful! New balance for " + target.getAccountNumber() + ": " + newBalance);
        } else {
            System.out.println("Failed to update balance in SQLite.");
        }
    }

    private static void handleWithdraw(Scanner scanner, AccountDAO accountDAO, String customerId) {
        Account target = pickCustomerAccount(scanner, accountDAO, customerId, "Select account to withdraw from");
        if (target == null) return;

        double amount = readPositiveAmount(scanner, "Enter withdrawal amount: ");
        if (amount <= 0) return;

        if (amount > target.getBalance()) {
            System.out.println("Insufficient balance. Available: " + target.getBalance());
            return;
        }

        double newBalance = target.getBalance() - amount;
        if (accountDAO.updateBalance(target.getAccountNumber(), newBalance)) {
            System.out.println("Withdrawal successful! New balance for " + target.getAccountNumber() + ": " + newBalance);
        } else {
            System.out.println("Failed to update balance in SQLite.");
        }
    }

    private static void handleTransfer(Scanner scanner, AccountDAO accountDAO, String customerId) {
        Account source = pickCustomerAccount(scanner, accountDAO, customerId, "Select source account");
        if (source == null) return;

        System.out.print("Enter destination account number: ");
        String destAccountNumber = scanner.nextLine().trim();
        if (destAccountNumber.isEmpty() || destAccountNumber.equals(source.getAccountNumber())) {
            System.out.println("Invalid or duplicate destination account number.");
            return;
        }

        List<Account> allAccounts = accountDAO.findAll();
        Account destination = null;
        if (allAccounts != null) {
            for (Account acc : allAccounts) {
                if (destAccountNumber.equals(acc.getAccountNumber())) {
                    destination = acc;
                    break;
                }
            }
        }

        if (destination == null) {
            System.out.println("Destination account [" + destAccountNumber + "] not found.");
            return;
        }

        double amount = readPositiveAmount(scanner, "Enter transfer amount: ");
        if (amount <= 0) return;

        if (amount > source.getBalance()) {
            System.out.println("Insufficient balance in source account. Available: " + source.getBalance());
            return;
        }

        double newSourceBalance = source.getBalance() - amount;
        double newDestBalance = destination.getBalance() + amount;

        if (!accountDAO.updateBalance(source.getAccountNumber(), newSourceBalance)) {
            System.out.println("Transfer aborted: failed to debit " + source.getAccountNumber());
            return;
        }

        if (!accountDAO.updateBalance(destination.getAccountNumber(), newDestBalance)) {
            accountDAO.updateBalance(source.getAccountNumber(), source.getBalance());
            System.out.println("Transfer failed while crediting destination; source balance restored.");
            return;
        }

        System.out.println("Transfer complete! " + amount + " moved from " + source.getAccountNumber()
                + " (new balance: " + newSourceBalance + ") to " + destination.getAccountNumber()
                + " (new balance: " + newDestBalance + ").");
    }

    private static void runStaffMenu(Scanner scanner, AccountDAO accountDAO, String role, BankStaff staff) {
        System.out.println("\nWelcome, " + staff.getEmail() + " (Role: " + role
                + ", Class: " + staff.getClass().getSimpleName() + ")");
        boolean running = true;

        while (running) {
            System.out.println("\n--- " + role + " Menu ---");
            System.out.println("1. View All Accounts in System");
            System.out.println("2. View Accounts by Customer ID");
            System.out.println("3. Set / Adjust Account Balance");
            System.out.println("4. Exit");
            System.out.print("Select option: ");

            String option = scanner.nextLine().trim();
            switch (option) {
                case "1" -> {
                    List<Account> allAccounts = accountDAO.findAll();
                    if (allAccounts == null || allAccounts.isEmpty()) {
                        System.out.println("No accounts found in database.");
                    } else {
                        for (Account acc : allAccounts) {
                            System.out.println("Account: " + acc.getAccountNumber()
                                    + " | Type: " + acc.getClass().getSimpleName()
                                    + " | Balance: " + acc.getBalance());
                        }
                    }
                }
                case "2" -> {
                    System.out.print("Enter Customer ID: ");
                    String custId = scanner.nextLine().trim();
                    displayCustomerAccounts(accountDAO, custId);
                }
                case "3" -> {
                    System.out.print("Enter Account Number: ");
                    String accNum = scanner.nextLine().trim();
                    System.out.print("Enter exact new balance: ");
                    try {
                        double newBalance = Double.parseDouble(scanner.nextLine().trim());
                        if (newBalance < 0) {
                            System.out.println("Balance cannot be negative.");
                            break;
                        }
                        if (accountDAO.updateBalance(accNum, newBalance)) {
                            System.out.println("Updated " + accNum + " balance to " + newBalance);
                        } else {
                            System.out.println("Account not found or update failed.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid balance format.");
                    }
                }
                case "4" -> running = false;
                default -> System.out.println("Invalid option.");
            }
        }
        System.out.println("Staff session ended.");
    }
}