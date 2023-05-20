package app.resources.body.history;

import app.resources.body.BodyController;
import app.resources.body.BodyControllerDefinition;
import javafx.fxml.FXML;
import javafx.scene.control.TreeTableView;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecution;

import java.util.List;

public class HistoryController implements BodyControllerDefinition {
    private BodyController bodyForHistoryExecutionController;
    @FXML
    private TreeTableView<tableRow> historyOfExecutionsFlowsTable;
    private List<FlowExecution> flowExecutions;


    private class tableRow{
        String flowName;
        String executionTime;
        String result;
    }

    @Override
    public void setFlowsDetails(List<FlowDefinition> flow) {

    }

    @Override
    public void show() {
        flowExecutions = bodyForHistoryExecutionController.getFlowExecutions();

    }

    @Override
    public void setBodyController(BodyController bodyCTRL) {
        bodyForHistoryExecutionController = bodyCTRL;
    }
}
