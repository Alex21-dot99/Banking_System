package boundary;

import control.AccountController;
import dao.AccountDAO;
import entity.Account;
import entity.Customer;
import entity.Transaction;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import dao.LoanRequestStore;
import entity.LoanRequest;
import java.util.Date;
import java.util.Optional;
import javafx.scene.control.TextInputDialog;

import java.util.List;

public class AccountView {

    private final Stage stage;
    private final Customer customer;
    private final AccountController accountController = new AccountController();
    private final AccountDAO accountDAO = new AccountDAO();

    public AccountView(Stage stage, Customer customer) {
        this.stage = stage;
        this.customer = customer;
    }

    public Scene createScene() {
        Label welcomeLabel = new Label("Welcome, " + customer.getFirstName() + " " + customer.getSurname());
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ComboBox<Account> accountSelector = new ComboBox<>();
        accountSelector.setMaxWidth(Double.MAX_VALUE);
        accountSelector.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Account acc, boolean empty) {
                super.updateItem(acc, empty);
                if (empty || acc == null) {
                    setText(null);
                } else {
                    setText(acc.getAccountNumber() + " (" + acc.getAccountType() + ") - P" + acc.getBalance());
                }
            }
        });
        accountSelector.setButtonCell(accountSelector.getCellFactory().call(null));

        Label balanceLabel = new Label("Balance: P0.00");
        TextField amountField = new TextField();
        amountField.setPromptText("Enter amount");

        Button depositButton = new Button("Deposit");
        Button withdrawButton = new Button("Withdraw");
        Button interestButton = new Button("Process Interest");
        Button requestLoanButton = new Button("Request Loan");
        Button logoutButton = new Button("Logout");

        Label statusLabel = new Label();
        ListView<String> transactionList = new ListView<>();
        transactionList.setPrefHeight(180);

        Runnable refreshView = () -> {
            Account selected = accountSelector.getValue();
            if (selected != null) {
                balanceLabel.setText(String.format("Current Balance: P%.2f", selected.getBalance()));
                transactionList.getItems().clear();
                for (Transaction tx : selected.getTransactions()) {
                    transactionList.getItems().add(
                            tx.getTimestamp() + " | " + tx.getTransactionType()
                                    + " | P" + tx.getAmount()
                                    + " | Balance: P" + tx.getBalanceAfter()
                    );
                }
            }
        };

        List<Account> accounts = accountDAO.findAccountsByCustomerId(customer.getUserId());
        accountSelector.setItems(FXCollections.observableArrayList(accounts));
        accountSelector.setOnAction(e -> refreshView.run());
        if (!accounts.isEmpty()) {
            accountSelector.getSelectionModel().selectFirst();
            refreshView.run();
        }

        depositButton.setOnAction(e -> {
            Account selected = accountSelector.getValue();
            if (selected == null) return;
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                boolean success = accountController.deposit(selected, amount);
                statusLabel.setText(success ? "Deposit recorded." : "Deposit failed.");
                if (success) {
                    amountField.clear();
                    refreshView.run();
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a valid number.");
            }
        });

        withdrawButton.setOnAction(e -> {
            Account selected = accountSelector.getValue();
            if (selected == null) return;
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                boolean success = accountController.withdraw(selected, amount);
                statusLabel.setText(success ? "Withdrawal recorded." : "Withdrawal failed.");
                if (success) {
                    amountField.clear();
                    refreshView.run();
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a valid number.");
            }
        });

        interestButton.setOnAction(e -> {
            Account selected = accountSelector.getValue();
            if (selected == null) return;
            boolean success = accountController.processInterest(selected);
            statusLabel.setText(success ? "Interest processed." : "Interest not applicable.");
            if (success) {
                refreshView.run();
            }
        });

        requestLoanButton.setOnAction(e -> {
            TextInputDialog amountDialog = new TextInputDialog(amountField.getText().trim());
            amountDialog.setTitle("Request Loan");
            amountDialog.setHeaderText("Submit a Loan Request");
            amountDialog.setContentText("Enter loan amount (BWP):");

            Optional<String> amountResult = amountDialog.showAndWait();
            if (amountResult.isEmpty()) return;

            double loanAmount;
            try {
                loanAmount = Double.parseDouble(amountResult.get().trim());
                if (loanAmount <= 0) {
                    statusLabel.setText("Loan amount must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a valid numeric loan amount.");
                return;
            }

            TextInputDialog purposeDialog = new TextInputDialog("Personal / Account Loan");
            purposeDialog.setTitle("Loan Purpose");
            purposeDialog.setHeaderText("Loan Purpose");
            purposeDialog.setContentText("Enter purpose:");
            String purpose = purposeDialog.showAndWait().map(String::trim).filter(s -> !s.isEmpty()).orElse("Loan Request");

            String reqId = LoanRequestStore.nextRequestId();
            LoanRequest req = new LoanRequest(reqId, customer, loanAmount, purpose, "PENDING", new Date());
            LoanRequestStore.addRequest(req);

            statusLabel.setText("Loan request " + reqId + " (BWP " + String.format("%.2f", loanAmount) + ") sent to Loan Officer.");
        });

        logoutButton.setOnAction(e -> {
            try {
                new LoginView().start(stage);
            } catch (Exception ex) {
                statusLabel.setText("Could not return to login.");
            }
        });

        HBox actions = new HBox(10, depositButton, withdrawButton, interestButton, requestLoanButton, logoutButton);
        VBox root = new VBox(
                12,
                welcomeLabel,
                new Label("Select Account:"),
                accountSelector,
                balanceLabel,
                amountField,
                actions,
                statusLabel,
                new Label("Transaction History:"),
                transactionList
        );
        root.setPadding(new Insets(20));

        return new Scene(root, 640, 480);
    }
}
