package app.resources.body.execution;

import app.resources.body.BodyController;
import app.resources.body.BodyControllerDefinition;
import app.resources.body.history.HistoryController;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.controlsfx.control.PopOver;
import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.FlowExecutionResult;
import project.java.stepper.flow.execution.context.StepExecutionContextImpl;
import project.java.stepper.flow.execution.runner.FLowExecutor;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import javafx.scene.control.Alert.AlertType;
import javafx.util.Duration;
import javafx.geometry.Insets;
import org.controlsfx.control.PopOver;
import javafx.geometry.Pos;
import javafx.animation.PauseTransition;

import javafx.scene.text.Text;

import java.awt.*;
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

    @FXML
    private StackPane flowExecutionInfo;

    @FXML
    private ProgressBar flowProgressBar;

    @FXML
    private Label stepResLabel;
    @FXML
    private TreeView<String> stepsProgressTreeView;
    @FXML
    private ListView<String> listOfLogs;
    @FXML
    private VBox theVboxParent;
    @FXML
    private VBox formalOutPutsVbox;

    List<FlowDefinition> flows;
    private PopOver errorPopOver;

    @Override
    public void setFlowsDetails(List<FlowDefinition> flow) {
        flows = flow;
    }

    @Override
    public void show() {
        flowDetailsExecutionBox.setVisible(false);
        flowExecutionInfo.setVisible(false);
        for (FlowDefinition flow : flows) {
            Button button = new Button(flow.getName());
            button.setOnAction(e -> handleFlowButtonAction(flow));
            flowListToExecute.getChildren().add(button);
        }
    }
    public void handleFlowButtonAction(FlowDefinition flowButton) {
        theVboxParent.getChildren().clear();
        theVboxParent.getChildren().addAll(flowDetailsExecutionBox,flowExecutionInfo);
        UUID uuid = UUID.randomUUID();
        FlowExecution flowExecution = new FlowExecution(uuid.toString(), flowButton);
        executeFlowButtonFinish.setDisable(false);
        flowDetailsExecutionBox.setDisable(false);
        flowExecutionInfo.setVisible(false);
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
        putInAccordionTheExecuteDetails();
        flowDetailsExecutionBox.setDisable(true);
        flowExecutionInfo.setVisible(true);
        stepResLabel.setText("");
        formalOutPutsVbox.getChildren().clear();
        stepsProgressTreeView.setRoot(new TreeItem<>());
        System.out.println("Starting execution of flow " + flow.getFlowDefinition().getName() + " [ID: " + flow.getUniqueId() + "]");
        bodyForFlowExecutionController.getFlowManagerExecution().exeFlow(flow);
        flow.getStepFinishedProperty().addListener((observable, oldValue, newValue) -> {
            flowProgressBar.setProgress(newValue.doubleValue() / (double)flow.getFlowDefinition().getFlowSteps().size());
        });
        flow.getFlowExecutionResultProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != FlowExecutionResult.PROCESSING) {
                stepResLabel.setText(newValue.name() + " [" + flow.getDuration() + ".ms" + "]");
                addFormalOutputs(flow);
            }
        });
        flow.getFlowContexts().getFlowStepsDataProperty().addListener((observable, oldValue, newValue) -> {
            stepsProgressTreeView.getRoot().getChildren().clear();
            listOfLogs.getItems().clear();
            for(StepUsageDeclaration step : flow.getFlowDefinition().getFlowSteps()){
                StepExecutionContextImpl.stepData data = flow.getFlowContexts().getStepData(step);
                TreeItem<String> stepNameItem = new TreeItem<>(step.getFinalStepName());
                TreeItem<String> stepDetails;
                if(data != null) {

                    if (!data.step.getFinalStepName().equals(data.step.getStepDefinition().name()))
                        stepNameItem.setValue(step.getFinalStepName() + "(" + step.getStepDefinition().name() + ")");

                    stepDetails = new TreeItem<>( "Total Time:[" + data.time.toMillis() + ".ms]" + ", Result:" + data.result);
                    TreeItem<String> stepSummaryLine = new TreeItem<>(data.stepSummaryLine);
                    TreeItem<String> totalLogs = new TreeItem<>("Total logs(" + data.logs.getStepLogs().size() + ")");
                    for (String log : data.logs.getStepLogs()) {
                        totalLogs.getChildren().add(new TreeItem<>(log));
                        listOfLogs.getItems().add(step.getFinalStepName() + ":" + log);
                    }
                    stepNameItem.getChildren().addAll(stepDetails,stepSummaryLine,totalLogs);
                    stepsProgressTreeView.getRoot().getChildren().addAll(stepNameItem);
                }
            }
        });
        //System.out.println("End execution of flow " + flow.getFlowDefinition().getName() + " [ID: " + flow.getUniqueId() + "]. Status: " + flow.getFlowExecutionResult());
      //  System.out.println("Outputs:");
       // for (Map.Entry<String, Object> entry : flow.getFormalOutPutsData().entrySet()) {
        //    System.out.println(entry.getKey() + ":\n" + entry.getValue());
        //}
        bodyForFlowExecutionController.addFlowExecutor(flow);
    }

    private void addFormalOutputs(FlowExecution flow) {
        Map<String, Object> formalOutputToData = flow.getFormalOutPutsData();
        Label Title = new Label("Formal outputs");
        Title.setFont(javafx.scene.text.Font.font(Font.BOLD));
        Title.setStyle("-fx-font-size: 14px;");
        for (Map.Entry<String, Object> entry : formalOutputToData.entrySet()) {
            Label outText;
            if (entry.getValue() == null)
                break;
            if (entry.getValue().getClass() == ListData.class) {
                outText = new Label(entry.getKey());
                HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.getChildren().add(outText);
                Hyperlink hyperlink = new Hyperlink("Show list");
                hyperlink.setOnAction(event -> showListPopup(entry.getValue()));
                hbox.getChildren().add(hyperlink);
                formalOutPutsVbox.getChildren().add(hbox);
            } else if (entry.getValue().getClass() == RelationData.class) {
                outText = new Label(entry.getKey());
                HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.getChildren().add(outText);
                Hyperlink hyperlink = new Hyperlink("Show table");
                hyperlink.setOnAction(event -> showTablePopup(entry.getValue()));
                hbox.getChildren().add(hyperlink);
                formalOutPutsVbox.getChildren().add(hbox);
            }
            else{
                outText = new Label(entry.getKey() + " [" + entry.getValue() + "]");
                formalOutPutsVbox.getChildren().add(outText);
            }
        }
    }

    private void putInAccordionTheExecuteDetails() {
        Accordion accordion = new Accordion();
        accordion.setPadding(new Insets(10,0,15,0));
        TitledPane titledPane = new TitledPane("Execution Details", flowDetailsExecutionBox);
        titledPane.setStyle("-fx-font-size: 16px;");
        VBox currentParent = (VBox) flowDetailsExecutionBox.getParent();
        ((Pane) currentParent).getChildren().clear();
        titledPane.setContent(flowDetailsExecutionBox);
        accordion.getPanes().add(titledPane);
        currentParent.getChildren().addAll(accordion,flowExecutionInfo);
    }

    private void showListPopup(Object flowData) {
        Stage popupStage = new Stage();
        ListView<String> tableView = new ListView<>();
        List<Object> list = ListData.class.cast(flowData).getList();
        list.forEach(item -> tableView.getItems().add(item.toString()));

        Scene popupScene = new Scene(tableView);
        popupStage.setScene(popupScene);
        popupStage.show();
    }
    private void showTablePopup(Object flowData) {
        Stage popupStage = new Stage();
        TableView<ObservableList<FlowsExecutionController.StringPropertyWrapper>> tableView = new TableView<>();
        //tableView.setColumnResizePolicy(FlowsExecutionController.getColumnResizePolicy());

        ObservableList<ObservableList<FlowsExecutionController.StringPropertyWrapper>> data = FXCollections.observableArrayList();
        RelationData table = (RelationData) flowData;

        for (String columnName : table.getColumns()) {
            final int columnIndex = table.getColumns().indexOf(columnName);
            TableColumn<ObservableList<FlowsExecutionController.StringPropertyWrapper>, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(cellData ->
                    cellData.getValue().get(columnIndex).getValueProperty());

            column.setCellFactory(TextFieldTableCell.forTableColumn());

            tableView.getColumns().add(column);
        }
        tableView.setItems(data);

        for(int i = 0; i < table.getRowsSize(); i++) {
            ObservableList<FlowsExecutionController.StringPropertyWrapper> row = FXCollections.observableArrayList();
            for (String value : table.getRowDataByColumnsOrder(i)) {
                row.add(new FlowsExecutionController.StringPropertyWrapper(value));
            }
            data.add(row);
        }
        Scene popupScene = new Scene(tableView);
        popupStage.setScene(popupScene);
        popupStage.show();
    }
    private static class StringPropertyWrapper {
        private final SimpleStringProperty valueProperty;

        public StringPropertyWrapper(String value) {
            this.valueProperty = new SimpleStringProperty(value);
        }

        public ObservableValue<String> getValueProperty() {
            return valueProperty;
        }
    }
}
