package app.resources.body.flowdefinition;

import app.resources.body.BodyController;
import app.resources.body.BodyControllerDefinition;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.ArrayList;
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
    @FXML private ImageView readOnlyImage;

    private List<FlowDefinition> flows;
    private List<Button> allFlowsButtons = new ArrayList<>();

    @Override
    public void show() {
        flowDetailsBox.setVisible(false);
        for (FlowDefinition flow : flows) {
            Button button = new Button(flow.getName());
            allFlowsButtons.add(button);
            button.setOnAction(e -> handleButtonAction(flow,button));
            flowListOfButtons.getChildren().add(button);
        }
    }

    private void handleButtonAction(FlowDefinition flowButton, Button button) {
        allFlowsButtons.stream().forEach(b -> b.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);"));
        button.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");

        flowDetailsBox.setVisible(true);
        FlowNameTL.setText(flowButton.getName());
        flowDescribtionTL.setText(flowButton.getDescription());
        addReadOnly(flowButton);

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
                freeInputsItem.getChildren().add(new TreeItem<>(key.getinputToFinalName().get(data.getName()) + ":" + data.userString() +
                        "(" + (!flowButton.getInitialValues().containsKey(key.getinputToFinalName().get(data.getName())) ? data.necessity() : "INITIANAL") + ")"));
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
    private void addReadOnly(FlowDefinition flowButton) {
        if(flowButton.isReadOnly()) {
            readOnlyImage.setVisible(true);
            Tooltip tooltip = new Tooltip();
            tooltip.setStyle("-fx-font-size: 12px;");
            readOnlyImage.setOnMouseEntered(event -> showReadOnly(tooltip,readOnlyImage,event));
            readOnlyImage.setOnMouseExited(event -> {
                tooltip.hide();
            });
        }
        else
            readOnlyImage.setVisible(false);
    }
    private void showReadOnly(Tooltip tooltip, ImageView data, MouseEvent event) {
        tooltip.setText("Read-Only");
        tooltip.show(data, event.getScreenX() + 5, event.getScreenY() + 5);
    }
}
