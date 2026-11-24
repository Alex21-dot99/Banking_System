package boundary;

import dao.AccountDAO;
import dao.TransactionDAO;
import entity.Account;
import entity.BankManager;
import entity.InvestmentAccount;
import entity.SavingsAccount;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class ManagerView {

    private final Stage stage;
    private final BankManager manager;
    private final AccountDAO accountDAO = new AccountDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    public ManagerView(Stage stage, BankManager manager) {
        this.stage = stage;
        this.manager = manager;
    }

    public Scene createScene() {
        Label headerLabel = new Label("Bank Manager Portal - " + manager.getName());
        headerLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label rulesLabel = new Label("Monthly Interest Rates: Investment = 5.00% | Savings = 0.05%");
        Label statusLabel = new Label();

        ObservableList<String> rows = FXCollections.observableArrayList();
        ListView<String> interestListView = new ListView<>(rows);
        interestListView.setPrefHeight(210);

        Button refreshButton = new Button("Review Interest Accounts");
        Button runInterestButton = new Button("Run Monthly Interest");
        Button logoutButton = new Button("Logout");

        Runnable loadAccounts = () -> {
            rows.clear();
            List<Account> accounts = accountDAO.findAll();
            for (Account acc : accounts) {
                double rate = getInterestRate(acc);
                if (rate > 0) {
                    double projectedInterest = acc.getBalance() * rate;
                    rows.add(String.format(
                            "%s | %s | Balance: BWP %.2f | Rate: %.2f%% | Interest: BWP %.2f",
                            acc.getAccountNumber(),
                            acc.getAccountType(),
                            acc.getBalance(),
                            rate * 100.0,
                            projectedInterest
                    ));
                }
            }
            statusLabel.setText("Loaded " + rows.size() + " interest-bearing account(s).");
        };

        refreshButton.setOnAction(e -> loadAccounts.run());

        runInterestButton.setOnAction(e -> {
            List<Account> accounts = accountDAO.findAll();
            int updatedCount = 0;

            for (Account acc : accounts) {
                double rate = getInterestRate(acc);
                if (rate > 0 && acc.getBalance() > 0) {
                    double interest = acc.getBalance() * rate;
                    double newBalance = acc.getBalance() + interest;
                    if (accountDAO.updateBalance(acc.getAccountNumber(), newBalance)) {
                        updatedCount++;
                    }
                }
            }

            loadAccounts.run();
            statusLabel.setText("Applied monthly interest to " + updatedCount + " account(s).");
        });

        logoutButton.setOnAction(e -> {
            try {
                new LoginView().start(stage);
            } catch (Exception ex) {
                statusLabel.setText("Could not return to login.");
            }
        });

        loadAccounts.run();

        HBox buttonRow = new HBox(8, refreshButton, runInterestButton, logoutButton);
        VBox root = new VBox(
                10,
                headerLabel,
                rulesLabel,
                interestListView,
                buttonRow,
                statusLabel
        );
        root.setPadding(new Insets(18));

        return new Scene(root, 740, 420);
    }

    private double getInterestRate(Account acc) {
        if (acc instanceof InvestmentAccount || "INVESTMENT".equalsIgnoreCase(acc.getAccountType())) {
            return 0.05; // 5%
        }
        if (acc instanceof SavingsAccount || "SAVINGS".equalsIgnoreCase(acc.getAccountType())) {
            return 0.0005; // 0.05%
        }
        return 0.0;
    }
}