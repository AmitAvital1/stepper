package app.resources.body.execution;

import app.resources.body.BodyController;
import app.resources.body.BodyControllerDefinition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;
import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.runner.FLowExecutor;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import javafx.geometry.Insets;
import org.controlsfx.control.PopOver;
import javafx.geometry.Pos;
import javafx.animation.PauseTransition;

import javafx.scene.text.Text;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.controlsfx.control.Notifications;
import project.java.stepper.step.api.DataNecessity;

public class FlowsExecutionController implements BodyControllerDefinition {

    private BodyController bodyForFlowExecutionController;

    @FXML
    private VBox flowDetailsExecutionBox;

    @FXML
    private VBox flowListToExecute;

    @FXML private VBox freeInputsList;

    @FXML
    private Label flowExecuteNameLabel;

    @FXML
    private Button executeFlowButtonFinish;

    List<FlowDefinition> flows;
    private PopOver errorPopOver;

    @Override
    public void setFlowsDetails(List<FlowDefinition> flow) {
        flows = flow;
    }

    @Override
    public void show() {
        flowDetailsExecutionBox.setVisible(false);
        for (FlowDefinition flow : flows) {
            Button button = new Button(flow.getName());
            button.setOnAction(e -> handleFlowButtonAction(flow));
            flowListToExecute.getChildren().add(button);
        }
    }
    public void handleFlowButtonAction(FlowDefinition flowButton) {
        UUID uuid = UUID.randomUUID();
        FlowExecution flowExecution = new FlowExecution(uuid.toString(), flowButton);
        executeFlowButtonFinish.setDisable(false);
        flowExecuteNameLabel.setText(flowButton.getName());
        freeInputsList.getChildren().clear();
        Map<StepUsageDeclaration, List<DataDefinitionDeclaration>> freeInputs = flowExecution.getFlowDefinition().getFlowFreeInputs();
        for (Map.Entry<StepUsageDeclaration, List<DataDefinitionDeclaration>> entry : freeInputs.entrySet()) {
            StepUsageDeclaration key = entry.getKey();
            List<DataDefinitionDeclaration> value = entry.getValue();
            for (DataDefinitionDeclaration dd : value) {
                HBox hbox = new HBox();
                hbox.setPadding(new Insets(10));

                Label stepName = new Label(key.getFinalStepName());
                TextField textField = new TextField();
                textField.setPromptText(dd.userString() + "[" + dd.dataDefinition().getName() + "]");
                Button button = new Button("Add");
                button.setOnAction(e -> handleFreeInputButtonAction(button,flowExecution,key,dd,textField));
                Label isMandatory = new Label(dd.necessity().toString());
                if(dd.necessity() == DataNecessity.MANDATORY)
                    executeFlowButtonFinish.setDisable(true);
                hbox.setSpacing(5);
                textField.setMaxWidth(250);
                hbox.setHgrow(textField, Priority.ALWAYS);
                hbox.getChildren().addAll(stepName, textField, button,isMandatory);
                freeInputsList.getChildren().addAll(hbox);
            }
        }
       executeFlowButtonFinish.setOnAction(e -> executeFlow(flowExecution));

        flowDetailsExecutionBox.setVisible(true);
    }

    private void handleFreeInputButtonAction(Button button, FlowExecution flowExecution, StepUsageDeclaration step, DataDefinitionDeclaration dd, TextField textField) {
        if(textField.isDisable()) {
            button.setText("Add");
            textField.setDisable(false);
            if(dd.necessity() == DataNecessity.MANDATORY)
                executeFlowButtonFinish.setDisable(true);
            return;
        }
        String text = textField.getText().trim();
       // Text messageText = new Text();
        if (text.isEmpty()) {
            showErrorMessage(button,"Please enter data.",step);
            return;
        }
        try{
            flowExecution.addFreeInputForStart(step, dd, text);
            flowExecution.validateToExecute();
            executeFlowButtonFinish.setDisable(false);
        }catch (MissMandatoryInput e) {
            //Pass
        }catch (StepperExeption e){
            showErrorMessage(button,e.getMessage(),step);
            return;
        }
        textField.setDisable(true);
        button.setText("Edit");
    }

    private void showErrorMessage(Button button,String message,StepUsageDeclaration step) {
        /*Notifications.create()
                .title("Error " + step.getFinalStepName())
                .text(message)
                .hideAfter(Duration.seconds(2))
                .showError();*/
        if (errorPopOver != null && errorPopOver.isShowing()) {
            errorPopOver.hide();
        }

        Label errorLabel = new Label(message);
        errorLabel.setStyle("-fx-text-fill: red;");
        errorPopOver = new PopOver();
        errorPopOver.setContentNode(new HBox(errorLabel));
        errorPopOver.setArrowLocation(PopOver.ArrowLocation.BOTTOM_CENTER);
        errorPopOver.setDetached(true);
        errorPopOver.show(button);

        // Hide the popover after 2 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> errorPopOver.hide());
        pause.play();
    }
    @Override
    public void setBodyController(BodyController bodyCTRL) {
        bodyForFlowExecutionController = bodyCTRL;
    }
    private void executeFlow(FlowExecution flow) {
        /*
        This function execute flow the user chose and validate all the free inputs has entered. then execute the flow.
         */

        System.out.println("Starting execution of flow " + flow.getFlowDefinition().getName() + " [ID: " + flow.getUniqueId() + "]");
        FLowExecutor fLowExecutor = new FLowExecutor();
        fLowExecutor.executeFlow(flow);
        System.out.println("End execution of flow " + flow.getFlowDefinition().getName() + " [ID: " + flow.getUniqueId() + "]. Status: " + flow.getFlowExecutionResult());
        System.out.println("Outputs:");
        for (Map.Entry<String, Object> entry : flow.getFormalOutPutsData().entrySet()) {
            System.out.println(entry.getKey() + ":\n" + entry.getValue());
        }
        bodyForFlowExecutionController.addFlowExecutor(flow);
    }
}