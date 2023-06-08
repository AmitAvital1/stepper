package app.resources.body.execution;

import app.resources.body.BodyController;
import app.resources.body.BodyControllerDefinition;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.*;
import org.controlsfx.control.PopOver;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.exceptions.MissMandatoryInput;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.FlowDefinitionImpl;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.FlowExecutionResult;
import project.java.stepper.flow.execution.context.StepExecutionContextImpl;
import project.java.stepper.step.api.DataDefinitionDeclaration;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.animation.PauseTransition;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.UIDDPresent;

public class FlowsExecutionController implements BodyControllerDefinition {

    private BodyController bodyForFlowExecutionController;

    @FXML
    private BorderPane theAllBorderOfExecute;

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
    private VBox thevboxforContandFormal;

    @FXML
    private TreeView<String> stepsProgressTreeView;
    @FXML
    private StackPane listOfLogsStackPane;

    @FXML
    private VBox theVboxParent;
    @FXML
    private Label supllyFreeInput;
    @FXML
    private HBox HBOXProccesing;
    @FXML
    private StackPane contAndFormalStack;

    List<FlowDefinition> flows;
    private PopOver errorPopOver;
    private List<Button> allFlowsButtons = new ArrayList<>();

    @Override
    public void setFlowsDetails(List<FlowDefinition> flow) {
        flows = flow;
    }

    public BorderPane getTheAllBorderOfExecute() {
        return theAllBorderOfExecute;
    }

    @Override
    public void show() {
        flowDetailsExecutionBox.setVisible(false);
        flowExecutionInfo.setVisible(false);
        if(bodyForFlowExecutionController.getFlowExecutions().size() > 0){
            if(bodyForFlowExecutionController.getFlowExecutions().get(bodyForFlowExecutionController.getFlowExecutions().size() - 1).getFlowExecutionResult() == FlowExecutionResult.PROCESSING) {
                flowDetailsExecutionBox.setVisible(true);
                flowExecutionInfo.setVisible(true);
            }
        }
        for (FlowDefinition flow : flows) {
            Button button = new Button(flow.getName());
            allFlowsButtons.add(button);
            button.setOnAction(e -> {handleFlowButtonAction(flow); allFlowsButtons.stream().forEach(b -> b.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);"));
                button.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");});
            flowListToExecute.getChildren().add(button);
        }
    }
    public void handleFlowButtonAction(FlowDefinition flowButton) {
        theVboxParent.getChildren().clear();
        theVboxParent.getChildren().addAll(flowDetailsExecutionBox,flowExecutionInfo);

        UUID uuid = UUID.randomUUID();
        FlowExecution flowExecution = new FlowExecution(uuid.toString(), flowButton);
        supllyFreeInput.setDisable(false);
        flowExecuteNameLabel.setDisable(false);
        freeInputsList.setDisable(false);
        flowExecutionInfo.setVisible(false);
        flowExecuteNameLabel.setText(flowButton.getName());
        freeInputsList.getChildren().clear();
        Map<String, DataDefinitionDeclaration> freeInputs = flowExecution.getFlowDefinition().getFreeInputFinalNameToDD();
        for (Map.Entry<String, DataDefinitionDeclaration> entry : freeInputs.entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclaration dd = entry.getValue();
                HBox hbox = new HBox();
                hbox.setPadding(new Insets(10));
                if(!flowButton.getInitialValues().containsKey(dd.getName())) {
                    Label stepName = new Label(key);
                    TextField textField = new TextField();
                    textField.setPromptText(dd.userString() + "[" + dd.dataDefinition().getName() + "]");
                    Button button = new Button("Add");
                    button.setOnAction(e -> handleFreeInputButtonAction(button, flowExecution, key, dd, textField));
                    if(dd.UIPresent() == UIDDPresent.FOLDER_DIALOG){
                        textField.setOnMouseClicked(e -> freeInputDialog(textField));
                    }
                    Label isMandatory = new Label(dd.necessity().toString());
                    if (dd.necessity() == DataNecessity.MANDATORY)
                        executeFlowButtonFinish.setDisable(true);
                    hbox.setSpacing(5);
                    textField.setMaxWidth(250);
                    hbox.setHgrow(textField, Priority.ALWAYS);
                    hbox.getChildren().addAll(stepName, textField, button, isMandatory);
                    freeInputsList.getChildren().addAll(hbox);
            }
        }
        try {
            flowExecution.validateToExecute();
            executeFlowButtonFinish.setDisable(false);
        } catch (MissMandatoryInput e) {
            executeFlowButtonFinish.setDisable(true);
        }
       executeFlowButtonFinish.setOnAction(e -> executeFlow(flowExecution));

        flowDetailsExecutionBox.setVisible(true);
    }

    private void freeInputDialog(TextField textField) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Directory");
        File selectedDir = directoryChooser.showDialog(bodyForFlowExecutionController.getMainController().getPrimaryStage());
        if(selectedDir != null)
            textField.setText(selectedDir.getAbsolutePath());
    }

    public void handleContinuationFlowButtonAction(FlowExecution flowExe) {
        theVboxParent.getChildren().clear();
        theVboxParent.getChildren().addAll(flowDetailsExecutionBox,flowExecutionInfo);
        FlowDefinition flowButton = flowExe.getFlowDefinition();
        supllyFreeInput.setDisable(false);
        flowExecuteNameLabel.setDisable(false);
        freeInputsList.setDisable(false);
        flowExecutionInfo.setVisible(false);
        flowExecuteNameLabel.setText(flowButton.getName());
        freeInputsList.getChildren().clear();
        Map<String, DataDefinitionDeclaration> freeInputs = flowExe.getFlowDefinition().getFreeInputFinalNameToDD();
        for (Map.Entry<String, DataDefinitionDeclaration> entry : freeInputs.entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclaration dd = entry.getValue();
            HBox hbox = new HBox();
            hbox.setPadding(new Insets(10));
            if (!flowButton.getInitialValues().containsKey(dd.getName())) {
                Label stepName = new Label(key);
                TextField textField = new TextField();
                Button button;
                if (flowExe.getStartersFreeInputForContext().containsKey(key)) {
                    textField.setPromptText(flowExe.getStartersFreeInputForContext().get(key).toString());
                    button = new Button("Edit");
                    textField.setDisable(true);
                } else {
                    textField.setPromptText(dd.userString() + "[" + dd.dataDefinition().getName() + "]");
                    button = new Button("Add");
                }
                button.setOnAction(e -> handleFreeInputButtonAction(button, flowExe, key, dd, textField));
                if(dd.UIPresent() == UIDDPresent.FOLDER_DIALOG){
                    textField.setOnMouseClicked(e -> freeInputDialog(textField));
                }
                Label isMandatory = new Label(dd.necessity().toString());
                if (dd.necessity() == DataNecessity.MANDATORY)
                    executeFlowButtonFinish.setDisable(true);
                hbox.setSpacing(5);
                textField.setMaxWidth(250);
                hbox.setHgrow(textField, Priority.ALWAYS);
                hbox.getChildren().addAll(stepName, textField, button, isMandatory);
                freeInputsList.getChildren().addAll(hbox);
            }
        }
        executeFlowButtonFinish.setOnAction(e -> executeFlow(flowExe));
        try {
            flowExe.validateToExecute();
            executeFlowButtonFinish.setDisable(false);
        } catch (MissMandatoryInput e) {
            executeFlowButtonFinish.setDisable(true);
        }
        flowDetailsExecutionBox.setVisible(true);
    }

    private void handleFreeInputButtonAction(Button button, FlowExecution flowExecution, String inputFinalName, DataDefinitionDeclaration dd, TextField textField) {
        if(textField.isDisable()) {
            button.setText("Add");
            textField.setDisable(false);
            if(dd.necessity() == DataNecessity.MANDATORY)
                executeFlowButtonFinish.setDisable(true);
            return;
        }
        String text = textField.getText().trim();
        if (text.isEmpty()) {
            showErrorMessage(button,"Please enter data.");
            return;
        }
        try{
            flowExecution.addFreeInputForStart(inputFinalName, dd, text);
            flowExecution.validateToExecute();
            executeFlowButtonFinish.setDisable(false);
        }catch (MissMandatoryInput e) {
            //Pass
        }catch (StepperExeption e){
            showErrorMessage(button,e.getMessage());
            return;
        }
        textField.setDisable(true);
        button.setText("Edit");
    }

    private void showErrorMessage(Button button,String message) {
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
        //flowDetailsExecutionBox.setDisable(true);
        supllyFreeInput.setDisable(true);
        flowExecuteNameLabel.setDisable(true);
        freeInputsList.setDisable(true);
        flowExecutionInfo.setVisible(true);


        Node n = HBOXProccesing.getChildren().get(0);
        HBOXProccesing.getChildren().clear();
        ProgressBar flowProgressBar = new ProgressBar(0);
       // HBOXProccesing.getChildren().add(flowProgressBar);
        Label stepResLabel = new Label("");
        //HBOXProccesing.getChildren().add(stepResLabel);
        HBOXProccesing.getChildren().addAll(n,flowProgressBar,stepResLabel);
        contAndFormalStack.getChildren().clear();
        VBox formalOutPutsVbox = new VBox();
        formalOutPutsVbox.setPrefHeight(200);
        formalOutPutsVbox.setPrefWidth(100);
        VBox continuationVBOX = new VBox();
        continuationVBOX.setSpacing(20);
        VBox formalAndCont = new VBox(formalOutPutsVbox,continuationVBOX);
        formalAndCont.setSpacing(10);
        contAndFormalStack.getChildren().addAll(formalAndCont);
        stepsProgressTreeView.setRoot(new TreeItem<>());
        System.out.println("Starting execution of flow " + flow.getFlowDefinition().getName() + " [ID: " + flow.getUniqueId() + "]");
        bodyForFlowExecutionController.getFlowManagerExecution().exeFlow(flow);
        bodyForFlowExecutionController.addFlowExecutor(flow);
        bodyForFlowExecutionController.enableExecutionButton();
        flow.getStepFinishedProperty().addListener((observable, oldValue, newValue) -> {
            flowProgressBar.setProgress(newValue.doubleValue() / (double)flow.getFlowDefinition().getFlowSteps().size());
        });
        flow.getFlowExecutionResultProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != FlowExecutionResult.PROCESSING) {
                if(flow.getUniqueId().equals(bodyForFlowExecutionController.getFlowExecutions().get(bodyForFlowExecutionController.getFlowExecutions().size()-1).getUniqueId())){
                    bodyForFlowExecutionController.disableExecutionButton();
                }
                stepResLabel.setText(newValue.name() + " [" + flow.getDuration() + ".ms" + "]");//TODO
                if(newValue == FlowExecutionResult.SUCCESS)
                    stepResLabel.setStyle("-fx-text-fill: green;");
                else if(newValue == FlowExecutionResult.WARNING)
                    stepResLabel.setStyle("-fx-text-fill: yellow;");
                else
                    stepResLabel.setStyle("-fx-text-fill: red;");
                addFormalOutputs(flow,formalOutPutsVbox);
                if(flow.getFlowDefinition().getFlowsContinuations().size() > 0) {
                    Label Title = new Label("Step continuation:");
                    formalOutPutsVbox.getChildren().add(Title);
                    HBox continuationHbox = new HBox();
                    continuationHbox.setSpacing(5);
                    ToggleGroup toggleGroup = new ToggleGroup();
                    Button continuationButton = new Button("Continue to flow");
                    continuationButton.setDisable(true);
                    continuationButton.setAlignment(Pos.CENTER);
                    for (FlowDefinitionImpl.continuationFlowDetails cDetails : flow.getFlowDefinition().getFlowsContinuations()) {
                        RadioButton radioButton = new RadioButton(cDetails.getTargetFlow().getName());
                        radioButton.setToggleGroup(toggleGroup);
                        radioButton.setOnAction(event -> {
                            if (radioButton.isSelected()) {
                                continuationButton.setDisable(false);
                                continuationButton.setOnAction(event1 -> {
                                    bodyForFlowExecutionController.executeContinuationFlowScreen(flow.runContinuationFlow(cDetails));
                                });
                            }
                        });
                        continuationHbox.getChildren().add(radioButton);
                    }
                    continuationVBOX.getChildren().addAll(Title,continuationHbox,continuationButton);
                }
            }
        });
        listOfLogsStackPane.getChildren().clear();
        ListView<String> listOfLogs = new ListView<>();
        listOfLogs.prefHeight(150);
        listOfLogsStackPane.getChildren().add(listOfLogs);
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
        executeFlowButtonFinish.setText("Rerun flow");
        executeFlowButtonFinish.setOnAction(e -> rerunFlow(flow));
    }

    private void rerunFlow(FlowExecution flow) {
        FlowExecution newFlow = flow.reRunFlow();
        bodyForFlowExecutionController.executeContinuationFlowScreen(newFlow);
        //Continuation behaving as rerun so using this function
    }

    private void addFormalOutputs(FlowExecution flow, VBox formalOutPutsVbox) {
        Map<String, Object> formalOutputToData = flow.getFormalOutPutsData();
        Label Title = new Label("Formal outputs:");
        formalOutPutsVbox.getChildren().add(Title);
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
