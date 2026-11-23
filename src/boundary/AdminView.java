package boundary;

import dao.AccountDAO;
import dao.CustomerDAO;
import dao.TransactionDAO;
import entity.Account;
import entity.BankAdministrator;
import entity.Customer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.UUID;

public class AdminView {

    private final Stage stage;
    private final BankAdministrator admin;
    private final CustomerDAO customerDAO = new CustomerDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public AdminView(Stage stage, BankAdministrator admin) {
        this.stage = stage;
        this.admin = admin;
    }

    public Scene createScene() {
        Label headerLabel = new Label("Bank Administrator Portal - " + admin.getName());
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label statusLabel = new Label();
        ListView<String> outputList = new ListView<>();
        outputList.setPrefHeight(200);

        // --- Open Customer Account Section ---
        TextField customerIdField = new TextField();
        customerIdField.setPromptText("Customer ID (e.g., CUST-001)");

        ComboBox<String> accountTypeBox = new ComboBox<>(FXCollections.observableArrayList(
                "SAVINGS",
                "INVESTMENT",
                "CHEQUE"
        ));
        accountTypeBox.getSelectionModel().selectFirst();

        TextField initialDepositField = new TextField();
        initialDepositField.setPromptText("Initial Deposit (BWP)");

        TextField employerField = new TextField();
        employerField.setPromptText("Employer Name (for Cheque)");

        Button openAccountButton = new Button("Open Account");
        Button viewCustomersButton = new Button("View Customers");
        Button viewTransactionsButton = new Button("View All Transactions");
        Button logoutButton = new Button("Logout");

        openAccountButton.setOnAction(e -> {
            String custId = customerIdField.getText().trim();
            String accType = accountTypeBox.getValue();
            String employer = employerField.getText().trim();

            Customer customer = customerDAO.findById(custId);
            if (customer == null) {
                statusLabel.setText("Customer not found: " + custId);
                return;
            }

            double deposit;
            try {
                deposit = Double.parseDouble(initialDepositField.getText().trim());
            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a valid initial deposit amount.");
                return;
            }

            if (("INVESTMENT".equals(accType) || "CHEQUE".equals(accType)) && deposit < 500.0) {
                statusLabel.setText(accType + " requires a minimum initial deposit of BWP 500.00.");
                return;
            }

            if ("CHEQUE".equals(accType) && employer.isEmpty()) {
                statusLabel.setText("Employee/employer details are required for a Cheque account.");
                return;
            }

            String generatedAccNo = "ACC-" + accType.substring(0, 3) + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            statusLabel.setText("Validated account " + generatedAccNo + " (" + accType + ") for " + customer.getFirstName() + ".");
            outputList.getItems().add(0, generatedAccNo + " | Customer: " + custId + " | Type: " + accType + " | Deposit: P" + deposit);
        });

        viewCustomersButton.setOnAction(e -> {
            outputList.getItems().clear();
            List<Customer> customers = customerDAO.findAll();
            for (Customer c : customers) {
                outputList.getItems().add(c.getUserId() + " | " + c.getFirstName() + " " + c.getSurname() + " | " + c.getEmail());
            }
            statusLabel.setText("Loaded " + customers.size() + " customer(s).");
        });

        viewTransactionsButton.setOnAction(e -> {
            outputList.getItems().clear();
            List<Account> allAccounts = accountDAO.findAll();
            for (Account acc : allAccounts) {
                outputList.getItems().add(acc.getAccountNumber() + " (" + acc.getAccountType() + ") | Balance: P" + acc.getBalance());
            }
            statusLabel.setText("Loaded " + allAccounts.size() + " account(s).");
        });

        logoutButton.setOnAction(e -> {
            try {
                new LoginView().start(stage);
            } catch (Exception ex) {
                statusLabel.setText("Could not return to login.");
            }
        });

        HBox accountInputs = new HBox(8, customerIdField, accountTypeBox, initialDepositField, employerField);
        HBox actionButtons = new HBox(8, openAccountButton, viewCustomersButton, viewTransactionsButton, logoutButton);

        VBox root = new VBox(
                10,
                headerLabel,
                new Label("Open Customer Account (Min BWP 500 for Investment/Cheque; Employer required for Cheque):"),
                accountInputs,
                actionButtons,
                statusLabel,
                outputList
        );
        root.setPadding(new Insets(18));

        return new Scene(root, 720, 420);
    }
}