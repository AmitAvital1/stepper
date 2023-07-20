package app.resources.main;

import app.resources.body.BodyController;
import app.resources.header.HeaderController;
import app.resources.header.HeaderDetailsRefresher;
import dto.FlowDefinitionDTO;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.stage.Stage;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.runner.FlowsExecutionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import static app.resources.util.Constants.REFRESH_RATE;


public class AppMainConroller {

    @FXML private Parent headerComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private Parent bodyComponent;
    @FXML private BodyController bodyComponentController;

    private Stage primaryStage;

    private List<FlowDefinition> flows = new ArrayList<>();
    private final List<FlowExecution> flowExecutions = new ArrayList<>();
    private List<FlowDefinitionDTO> flowsDTO = new ArrayList<>();



    private FlowsExecutionManager flowsExecutionManager = new FlowsExecutionManager();

    @FXML
    public void initialize() {
        if (headerComponentController != null && bodyComponentController != null) {
            headerComponentController.setMainController(this);
            bodyComponentController.setMainController(this);
        }
    }

    public void showFlowDefinition() {
        bodyComponentController.showFlowDefinition();
    }
    public void showFlowsHistory(){
        bodyComponentController.showFlowHistory();
    }
    public void showFlowsStats() {bodyComponentController.showFlowStats();}

    public void showFlowExectuion(){ bodyComponentController.showFlowExecution(); }
    public void addFlows(List<FlowDefinition> newFlows,List<FlowDefinitionDTO> flowsDTO){flows = newFlows;
        this.flowsDTO = flowsDTO;
    }
    public List<FlowDefinition> getFlows(){return flows;}
    public List<FlowDefinitionDTO> getFlowsDTO(){return flowsDTO;}
    public void addExecutorFlow(FlowExecution flowExecution){
        this.flowExecutions.add(flowExecution);
        headerComponentController.setFlowHistory();//Set the button available
    }
    public void setHistoryButton(){
        headerComponentController.setFlowHistory();//Set the button available
    }
    public List<FlowExecution> getFlowExecutions(){return flowExecutions;}

    public FlowsExecutionManager getFlowsExecutionManager() {
        return flowsExecutionManager;
    }


    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public HeaderController getHeaderComponentController() {
        return headerComponentController;
    }

    public void clearBodyScreen() {
        bodyComponentController.clearScreen();
    }

    public void shutDown(){
        bodyComponentController.shutDown();
        headerComponentController.close();
    }
}
