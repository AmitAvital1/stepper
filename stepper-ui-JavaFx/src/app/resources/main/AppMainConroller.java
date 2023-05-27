package app.resources.main;

import app.resources.body.BodyController;
import app.resources.header.HeaderController;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;

import java.util.ArrayList;
import java.util.List;


public class AppMainConroller {

    @FXML private Parent headerComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private Parent bodyComponent;
    @FXML private BodyController bodyComponentController;

    private List<FlowDefinition> flows = new ArrayList<>();
    private final List<FlowExecution> flowExecutions = new ArrayList<>();

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

    public void showFlowExectuion(){ bodyComponentController.showFlowExecution(); }
    public void addFlows(List<FlowDefinition> newFlows){flows = newFlows;}
    public List<FlowDefinition> getFlows(){return flows;}
    public void addExecutorFlow(FlowExecution flowExecution){
        this.flowExecutions.add(flowExecution);
        headerComponentController.setFlowHistory();//Set the button available
    }
    public List<FlowExecution> getFlowExecutions(){return flowExecutions;}
}
