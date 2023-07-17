package app.resources.body;

import app.resources.body.execution.AdminFlowsExecutionController;
import app.resources.header.AdminHeaderController;
import app.resources.main.AdminAppMainConroller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.runner.FlowsExecutionManager;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class AdminBodyController {

    private AdminAppMainConroller mainController;
    private AdminHeaderController headerController;
    @FXML
    private StackPane bodyPane;

    private URL lastLoadedExecution;
    private Parent lastScreen;
    FXMLLoader lastfxmlLoader;
    private AdminBodyControllerDefinition saveLastControllerExecution;
    private Timer timer = null;

    public void setMainController(AdminAppMainConroller mainController) {
        this.mainController = mainController;
    }

    public void clearScreen(){
        bodyPane.getChildren().clear();
    }

    public void showFlowDefinition() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/flowdefinition/userManagement.fxml");
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
            bController.setFlowsDetails(new ArrayList<>(),mainController.getFlows());
            bController.setBodyController(this);
            bController.show();
            bodyPane.getChildren().setAll(screen);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeExistFlowScreen(FlowDefinition flow) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/execution/flowExecution.fxml");
        fxmlLoader.setLocation(url);
        try {
            Parent screen = fxmlLoader.load(url.openStream());
            AdminFlowsExecutionController bController = fxmlLoader.getController();
            bController.setFlowsDetails(new ArrayList<>(),mainController.getFlows());
            bController.setBodyController(this);
            bController.show();
            bController.handleFlowButtonAction(flow);
            bodyPane.getChildren().setAll(screen);
            lastLoadedExecution = url;
            saveLastControllerExecution = fxmlLoader.getController();//To return to execute
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void executeContinuationFlowScreen(FlowExecution flow) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/execution/flowExecution.fxml");
        fxmlLoader.setLocation(url);
        try {
            Parent screen = fxmlLoader.load(url.openStream());
            AdminFlowsExecutionController bController = fxmlLoader.getController();
            bController.setFlowsDetails(new ArrayList<>(),mainController.getFlows());
            bController.setBodyController(this);
            bController.show();
            bController.handleContinuationFlowButtonAction(flow);
            bodyPane.getChildren().setAll(screen);
            lastLoadedExecution = url;
            saveLastControllerExecution = fxmlLoader.getController();//To return to execute
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addFlowExecutor(FlowExecution flowExecution){mainController.addExecutorFlow(flowExecution);}
    public List<FlowExecution> getFlowExecutions(){return mainController.getFlowExecutions();}
    public FlowsExecutionManager getFlowManagerExecution(){return mainController.getFlowsExecutionManager();}
    public AdminAppMainConroller getMainController() {return mainController;}

    public void disableExecutionButton() {
        mainController.getHeaderComponentController().disableExecutionButton();
    }

    public void enableExecutionButton() {
        mainController.getHeaderComponentController().enableExecutionButton();
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    public void close(){
        if(timer != null)
            timer.cancel();
    }
}
