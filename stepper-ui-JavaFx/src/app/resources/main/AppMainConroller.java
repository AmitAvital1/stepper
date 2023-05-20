package app.resources.main;

import app.resources.body.BodyController;
import app.resources.header.HeaderController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.runner.FLowExecutor;
import project.java.stepper.load.LoadStepperDataFromXml;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public void showFlowExectuion(){ bodyComponentController.showFlowExectuion(); }
    public void addFlows(List<FlowDefinition> newFlows){flows = newFlows;}
    public List<FlowDefinition> getFlows(){return flows;}
    public void addExecutorFlow(FlowExecution flowExecution){this.flowExecutions.add(flowExecution);}
    public List<FlowExecution> getFlowExecutions(){return flowExecutions;}
}
