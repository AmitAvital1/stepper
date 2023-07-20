package app.resources.body;

import app.resources.main.AdminAppMainConroller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;

public class AdminBodyController {

    private AdminAppMainConroller mainController;
    @FXML
    private StackPane bodyPane;

    private Timer timer = null;

    public void setMainController(AdminAppMainConroller mainController) {
        this.mainController = mainController;
    }

    public void clearScreen(){
        bodyPane.getChildren().clear();
    }

    public void showFlowDefinition() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/usermanagement/userManagement.fxml");
        fxmlLoader.setLocation(url);
        loadScreen(fxmlLoader, url);
    }
    public void showFlowExecution() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/roles/rolesManagement.fxml");
        fxmlLoader.setLocation(url);
        loadScreen(fxmlLoader, url);
    }
    public void showFlowHistory() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/history/history.fxml");
        fxmlLoader.setLocation(url);
        loadScreen(fxmlLoader, url);
    }
    public void showFlowStats() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/statistics/statistics.fxml");
        fxmlLoader.setLocation(url);
        loadScreen(fxmlLoader, url);
    }
    private void loadScreen(FXMLLoader fxmlLoader,URL url){
        try {

            if(this.timer != null)
                timer.cancel();

            Parent screen = fxmlLoader.load(url.openStream());
            AdminBodyControllerDefinition bController = fxmlLoader.getController();
            bController.setFlowsDetails(mainController.getFlows());
            bController.setBodyController(this);
            bController.show();
            bodyPane.getChildren().setAll(screen);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public AdminAppMainConroller getMainController() {return mainController;}

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    public void close(){
        if(timer != null)
            timer.cancel();
    }
}
