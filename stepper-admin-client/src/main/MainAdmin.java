
package main;

import app.resources.main.AdminAppMainConroller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

public class MainAdmin extends Application {
    public static final String APP_FXML_INCLUDE_RESOURCE = "/app/resources/main/app.fxml";

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource(APP_FXML_INCLUDE_RESOURCE);
        fxmlLoader.setLocation(url);
        Parent root = fxmlLoader.load(url.openStream());

        Scene scene = new Scene(root, 900, 600);
        scene.getStylesheets().add(getClass().getResource("/app/resources/main/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setTitle("Stepper - Admin");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/app/resources/img/stepper-logo2.png")));

        AdminAppMainConroller controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
        primaryStage.setOnCloseRequest(event -> {
            controller.shutDown();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}