package app.resources.body;

import app.resources.body.execution.FlowsExecutionController;
import app.resources.header.HeaderController;
import app.resources.main.AppMainConroller;
import dto.FlowDefinitionDTO;
import dto.execution.FlowExecutionDTO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.FlowExecutionResult;
import project.java.stepper.flow.execution.runner.FlowsExecutionManager;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Timer;

public class BodyController {

    private AppMainConroller mainController;
    private HeaderController headerController;
    @FXML
    private StackPane bodyPane;

    private URL lastLoadedExecution;
    private Parent lastScreen;
    FXMLLoader lastfxmlLoader;
    private BodyControllerDefinition saveLastControllerExecution;
    private String CurrentUUID = null;

    public void setCurrentFlow(FlowExecutionDTO currentFlow) {
        this.currentFlow = currentFlow;
    }

    FlowExecutionDTO currentFlow = null;

    private Timer timer = null;

    public void setMainController(AppMainConroller mainController) {
        this.mainController = mainController;
    }

    public void clearScreen(){
        bodyPane.getChildren().clear();
    }
    public void showFlowDefinition() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/flowdefinition/flowDefinition.fxml");
        fxmlLoader.setLocation(url);
        loadScreen(fxmlLoader, url);
    }
    public void showFlowExecution() {
        FXMLLoader fxmlLoader = new FXMLLoader();
        if(currentFlow != null) {
            if (currentFlow.getFlowExecutionResult() == FlowExecutionResult.PROCESSING)
                if (lastLoadedExecution != null) {
                    bodyPane.getChildren().setAll(((FlowsExecutionController)saveLastControllerExecution).getTheAllBorderOfExecute());
                    return;
                }
        }

        URL url = getClass().getResource("/app/resources/body/execution/flowExecution.fxml");
        fxmlLoader.setLocation(url);
        loadScreen(fxmlLoader, url);
        lastLoadedExecution = url;
        saveLastControllerExecution = fxmlLoader.getController();//To return to execute


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
            Parent screen = fxmlLoader.load(url.openStream());
            BodyControllerDefinition bController = fxmlLoader.getController();
            bController.setFlowsDetails(mainController.getFlows(),mainController.getFlowsDTO());
            bController.setBodyController(this);
            bController.show();
            bodyPane.getChildren().setAll(screen);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void executeExistFlowScreen(FlowDefinitionDTO flow) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/execution/flowExecution.fxml");
        fxmlLoader.setLocation(url);
        try {
            Parent screen = fxmlLoader.load(url.openStream());
            FlowsExecutionController bController = fxmlLoader.getController();
            bController.setFlowsDetails(mainController.getFlows(),mainController.getFlowsDTO());
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
    public void executeContinuationFlowScreen(FlowExecutionDTO flow) {
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("/app/resources/body/execution/flowExecution.fxml");
        fxmlLoader.setLocation(url);
        try {
            Parent screen = fxmlLoader.load(url.openStream());
            FlowsExecutionController bController = fxmlLoader.getController();
            bController.setFlowsDetails(mainController.getFlows(),mainController.getFlowsDTO());
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
    public AppMainConroller getMainController() {return mainController;}

    public void disableExecutionButton() {
        mainController.getHeaderComponentController().disableExecutionButton();
    }

    public void enableExecutionButton() {
        mainController.getHeaderComponentController().enableExecutionButton();
    }

    public String getCurrentUUID() {
        return CurrentUUID;
    }

    public void setCurrentUUID(String currentUUID) {
        CurrentUUID = currentUUID;
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }
    public void shutDown(){
        if(timer != null)
            timer.cancel();
    }
}
