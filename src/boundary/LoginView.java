package boundary;

import entity.BankAdministrator;
import entity.BankManager;
import entity.LoanOfficer;

import control.LoginController;
import entity.Customer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginView extends Application {

    private final LoginController loginController = new LoginController();

    @Override
    public void start(Stage primaryStage) {
        Label headingLabel = new Label("Banking System Login");
        headingLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("Customer", "Bank Administrator", "Loan Officer", "Bank Manager");
        roleComboBox.setValue("Customer");
        roleComboBox.setMaxWidth(Double.MAX_VALUE);


        Button loginButton = new Button("Login");
        loginButton.setMaxWidth(Double.MAX_VALUE);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #c62828;");

        loginButton.setOnAction(event -> {
            String email = emailField.getText().trim();
            String password = passwordField.getText();
            String selectedRole = roleComboBox.getValue();
            LoginController.LoginResult result = loginController.authenticate(selectedRole, email, password);

            if (result != null) {
                errorLabel.setText("");
                switch (result.getRole()) {
                    case "Customer" -> {
                        AccountView accountView = new AccountView(primaryStage, result.getCustomer());
                        primaryStage.setScene(accountView.createScene());
                    }
                    case "Bank Administrator" -> {
                        AdminView adminView = new AdminView(primaryStage, (BankAdministrator) result.getStaff());
                        primaryStage.setScene(adminView.createScene());
                    }
                    case "Loan Officer" -> {
                        LoanOfficerView loanView = new LoanOfficerView(primaryStage, (LoanOfficer) result.getStaff());
                        primaryStage.setScene(loanView.createScene());
                    }
                    case "Bank Manager" -> {
                        ManagerView managerView = new ManagerView(primaryStage, (BankManager) result.getStaff());
                        primaryStage.setScene(managerView.createScene());
                    }
                    default -> errorLabel.setText("Unsupported role selected.");
                }
            } else {
                errorLabel.setText("Invalid role, email, or password.");
            }
        });

        VBox root = new VBox(12, headingLabel, roleComboBox, emailField, passwordField, loginButton, errorLabel);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.CENTER);

        primaryStage.setTitle("Banking System - Login");
        primaryStage.setScene(new Scene(root, 360, 310));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}