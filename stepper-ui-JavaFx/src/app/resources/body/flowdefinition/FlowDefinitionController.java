package app.resources.body.flowdefinition;

import app.resources.body.BodyController;
import app.resources.body.BodyControllerDefinition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.List;
import java.util.Map;

public class FlowDefinitionController implements BodyControllerDefinition {
    @FXML private Parent bodyForFlowDefinition;
    @FXML private BodyController bodyForFlowDefinitionController;

    @FXML private VBox flowListOfButtons;
    @FXML private Label FlowNameTL;
    @FXML private Label flowDescribtionTL;
    @FXML private VBox flowDetailsBox;
    @FXML private TreeView<String> flowDetailsTreeView;
    @FXML private Button executeFlowDFButton;

    List<FlowDefinition> flows;

    @Override
    public void show() {
        flowDetailsBox.setVisible(false);

        for (FlowDefinition flow : flows) {
            Button button = new Button(flow.getName());
            button.setOnAction(e -> handleButtonAction(flow));
            flowListOfButtons.getChildren().add(button);
        }
    }

    private void handleButtonAction(FlowDefinition flowButton) {
        //DONT FORGET PUT SOMEWHERE THE READ ONLY!!!!!!!!!!!!!!!
        flowDetailsBox.setVisible(true);
        FlowNameTL.setText(flowButton.getName());
        flowDescribtionTL.setText(flowButton.getDescription());

        flowDetailsTreeView.setRoot(new TreeItem<>());
        TreeItem<String> formalOutputsItem = new TreeItem<>("Formal outputs");

        for(Map.Entry<String, DataDefinitionDeclaration> entry : flowButton.getFormalOutput().entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclaration value = entry.getValue();
            formalOutputsItem.getChildren().add(new TreeItem<>(key + ":" + value.userString()));
        }

        TreeItem<String> flowStepsItem = new TreeItem<>("Flow steps");
        for(StepUsageDeclaration step : flowButton.getFlowSteps())
        {
            if(step.getFinalStepName().equals(step.getStepDefinition().name()))
                flowStepsItem.getChildren().add(new TreeItem<>(step.getFinalStepName() + ", Read Only:" + step.getStepDefinition().isReadonly()));
            else
                flowStepsItem.getChildren().add(new TreeItem<>(step.getStepDefinition().name() + " Alias to:" + step.getFinalStepName() + ", Read Only:" + step.getStepDefinition().isReadonly()));
        }
        TreeItem<String> freeInputsItem = new TreeItem<>("Free inputs");
        for(Map.Entry<StepUsageDeclaration, List<DataDefinitionDeclaration>> entry : flowButton.getFlowFreeInputs().entrySet()) {
            StepUsageDeclaration key = entry.getKey();
            List<DataDefinitionDeclaration> value = entry.getValue();
            for(DataDefinitionDeclaration data : value) {
                freeInputsItem.getChildren().add(new TreeItem<>(key.getinputToFinalName().get(data.getName()) + ":" + data.userString() + "(" + data.necessity() + ")"));
                freeInputsItem.getChildren().add(new TreeItem<>("Data type:" + data.dataDefinition().getName()));
                freeInputsItem.getChildren().add(new TreeItem<>(""));
            }
        }
        flowDetailsTreeView.getRoot().getChildren().addAll(formalOutputsItem,flowStepsItem,freeInputsItem);
        flowDetailsTreeView.refresh();
        executeFlowDFButton.setOnAction(e -> executeFlowDFButton(flowButton));

    }
    @Override
    public void setFlowsDetails(List<FlowDefinition> flow) {
        flows = flow;
    }

    void executeFlowDFButton(FlowDefinition flow) {
        bodyForFlowDefinitionController.executeExistFlowScreen(flow);
    }
    @Override
    public void setBodyController(BodyController bodyCTRL) {
        bodyForFlowDefinitionController = bodyCTRL;
    }
}
