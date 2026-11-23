package boundary;

import entity.LoanOfficer;
import entity.LoanRequest;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import dao.CustomerDAO;
import entity.Customer;

public class LoanOfficerView {

    private final Stage stage;
    private final LoanOfficer loanOfficer;

    public LoanOfficerView(Stage stage, LoanOfficer loanOfficer) {
        this.stage = stage;
        this.loanOfficer = loanOfficer;
    }

    public Scene createScene() {
        Label headerLabel = new Label("Loan Officer Portal - " + loanOfficer.getName());
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label statusLabel = new Label("Select a loan request to review, or enter a new request.");

        ObservableList<String> loanItems = FXCollections.observableArrayList(
                "LN-001 | Customer: CUST-001 | Amount: BWP 15000.00 | Status: PENDING",
                "LN-002 | Customer: CUST-002 | Amount: BWP 8500.00 | Status: PENDING"
        );
        ListView<String> loanListView = new ListView<>(loanItems);
        loanListView.setPrefHeight(200);

        TextField customerIdField = new TextField();
        customerIdField.setPromptText("Customer ID (e.g., CUST-001)");

        TextField amountField = new TextField();
        amountField.setPromptText("Loan Amount (BWP)");

        Button addSampleButton = new Button("Add Request");
        Button approveButton = new Button("Approve Loan");
        Button denyButton = new Button("Deny Loan");
        Button logoutButton = new Button("Logout");

        addSampleButton.setOnAction(e -> {
            String custId = customerIdField.getText().trim();
            if (custId.isEmpty()) {
                statusLabel.setText("Enter a Customer ID.");
                return;
            }
            double amount;
            try {
                amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) {
                    statusLabel.setText("Loan amount must be greater than 0.");
                    return;
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Enter a valid numeric loan amount.");
                return;
            }

            String reqId = "LN-" + (loanItems.size() + 101);
   // Instantiates entity.LoanRequest to keep your domain model connected
            Customer matchedCustomer = new CustomerDAO().findById(custId);
            LoanRequest req = new LoanRequest(
                    reqId,
                    matchedCustomer,
                    amount,
                    "Loan Request",
                    "PENDING",
                    new java.util.Date()
            );
            loanItems.add(0, reqId + " | Customer: " + custId + " | Amount: BWP " + String.format("%.2f", amount) + " | Status: PENDING");
            loanItems.add(0, reqId + " | Customer: " + custId + " | Amount: BWP " + String.format("%.2f", amount) + " | Status: PENDING");
            customerIdField.clear();
            amountField.clear();
            statusLabel.setText("Added " + reqId + " for " + custId + ".");
        });

        approveButton.setOnAction(e -> updateSelectedLoanStatus(loanListView, loanItems, statusLabel, "APPROVED"));
        denyButton.setOnAction(e -> updateSelectedLoanStatus(loanListView, loanItems, statusLabel, "DENIED"));

        logoutButton.setOnAction(e -> {
            try {
                new LoginView().start(stage);
            } catch (Exception ex) {
                statusLabel.setText("Could not return to login.");
            }
        });

        HBox addRow = new HBox(8, customerIdField, amountField, addSampleButton);
        HBox actionRow = new HBox(8, approveButton, denyButton, logoutButton);

        VBox root = new VBox(
                10,
                headerLabel,
                new Label("Review & Decide on Loan Requests:"),
                loanListView,
                addRow,
                actionRow,
                statusLabel
        );
        root.setPadding(new Insets(18));

        return new Scene(root, 720, 420);
    }

    private void updateSelectedLoanStatus(
            ListView<String> listView,
            ObservableList<String> items,
            Label statusLabel,
            String newStatus
    ) {
        int index = listView.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            statusLabel.setText("Please select a loan request first.");
            return;
        }
        String current = items.get(index);
        String updated = current.replaceAll("Status: \\w+", "Status: " + newStatus);
        items.set(index, updated);
        statusLabel.setText("Loan marked as " + newStatus + " by " + loanOfficer.getName() + ".");
    }
}