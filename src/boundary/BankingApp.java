package boundary;

import javafx.application.Application;
import javafx.stage.Stage;

public class BankingApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        new LoginView().start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}