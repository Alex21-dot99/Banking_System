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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;


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
        Button registerCustomerButton = new Button("Register Customer");
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

        registerCustomerButton.setOnAction(e -> showRegisterCustomerDialog(statusLabel, outputList, customerIdField));

        HBox accountInputs = new HBox(8, customerIdField, accountTypeBox, initialDepositField, employerField);
        HBox actionButtons = new HBox(8, registerCustomerButton, openAccountButton, viewCustomersButton, viewTransactionsButton, logoutButton);

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

        return new Scene(root, 820, 450);
    }
    private void showRegisterCustomerDialog(Label statusLabel, ListView<String> outputList, TextField customerIdField) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Register Customer");
        dialog.setHeaderText("Register a New Customer");

        ButtonType saveButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));

        TextField userIdField = new TextField("CUST-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase());
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField surnameField = new TextField();
        surnameField.setPromptText("Surname");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        grid.addRow(0, new Label("Customer ID:"), userIdField);
        grid.addRow(1, new Label("First Name:"), firstNameField);
        grid.addRow(2, new Label("Surname:"), surnameField);
        grid.addRow(3, new Label("Username:"), usernameField);
        grid.addRow(4, new Label("Email:"), emailField);
        grid.addRow(5, new Label("Phone:"), phoneField);
        grid.addRow(6, new Label("Address:"), addressField);
        grid.addRow(7, new Label("Password:"), passwordField);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(result -> {
            if (result == saveButtonType) {
                String custId = userIdField.getText().trim();
                String firstName = firstNameField.getText().trim();
                String surname = surnameField.getText().trim();
                String username = usernameField.getText().trim();
                String email = emailField.getText().trim();
                String phone = phoneField.getText().trim();
                String address = addressField.getText().trim();
                String password = passwordField.getText().trim();

                if (custId.isEmpty() || firstName.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    statusLabel.setText("Please fill in ID, First Name, Surname, Email, and Password.");
                    return;
                }

                boolean saved = customerDAO.insertCustomer(
                        custId, firstName, surname, username, email, phone, address, password
                );

                if (saved) {
                    statusLabel.setText("Registered customer " + custId + " (" + firstName + " " + surname + ").");
                    outputList.getItems().add(0, custId + " | " + firstName + " " + surname + " | " + email);
                    customerIdField.setText(custId);
                } else {
                    statusLabel.setText("Could not register customer in database.");
                }
            }
        });
    }
}