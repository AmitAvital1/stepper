package app.resources.body.history;

import app.resources.body.BodyController;
import app.resources.body.BodyControllerDefinition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import project.java.stepper.dd.impl.DataDefinitionRegistry;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.definition.api.StepUsageDeclaration;
import project.java.stepper.flow.execution.FlowExecution;
import project.java.stepper.flow.execution.FlowExecutionResult;
import project.java.stepper.flow.execution.context.StepExecutionContextImpl;
import project.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.ArrayList;
import java.util.List;

public class HistoryController implements BodyControllerDefinition {
    private BodyController bodyForHistoryExecutionController;
    @FXML
    private TableView<tableRow> historyOfExecutionsFlowsTable;
    @FXML
    private TableColumn<tableRow, String> nameColumn;

    @FXML
    private TableColumn<tableRow, String> executionTimeColum;

    @FXML
    private TableColumn<tableRow, String> resultColumn;

    @FXML
    private Label flowChooseName;

    @FXML
    private Label flowChooseId;
    @FXML
    private VBox flowChooseVBOX;
    @FXML
    private ListView<String> freeInputsList;
    @FXML
    private VBox flowOutputsList;
    @FXML
    private TreeView<String> stepDetailsTree;


    private List<FlowExecution> flowExecutions;
    private List<FlowDefinition> flows;


    public class tableRow{
        private String flowName;
        private String executionTime;
        private ObjectProperty<FlowExecutionResult> result = new SimpleObjectProperty<>();
        private FlowExecution theFlow;

        public tableRow(String n, String e, String r, FlowExecution f){
            flowName = n;
            executionTime = e;
            theFlow = f;
            result.set(theFlow.getFlowExecutionResult());
            //result.bind(theFlow.getFlowExecutionResultProperty());
            theFlow.getFlowExecutionResultProperty().addListener((observable, oldValue, newValue) -> {
                // Update the table row when the result property changes
                setResult(newValue);
            });
        }

        public void setTheFlow(FlowExecution theFlow) {
            this.theFlow = theFlow;
        }

        public FlowExecution getTheFlow() {
            return theFlow;
        }

        public String getExecutionTime() {
            return executionTime;
        }

        public String getResult() {
            return result.get().toString();
        }

        public String getFlowName() {
            return flowName;
        }

        public void setExecutionTime(String executionTime) {
            this.executionTime = executionTime;
        }

        public void setFlowName(String flowName) {
            this.flowName = flowName;
        }

        public void setResult(FlowExecutionResult result) {
            this.result.set(result);
        }
    }

    @Override
    public void setFlowsDetails(List<FlowDefinition> flow) {
        flows = flow;
    }

    @Override
    public void show() {
        flowChooseVBOX.setVisible(false);
        flowExecutions = bodyForHistoryExecutionController.getFlowExecutions();
        nameColumn.setCellValueFactory(new PropertyValueFactory<tableRow, String>("flowName"));
        executionTimeColum.setCellValueFactory(new PropertyValueFactory<tableRow, String>("executionTime"));
        //resultColumn.setCellValueFactory(new PropertyValueFactory<tableRow, String>("result"));
        resultColumn.setCellValueFactory(cellData -> cellData.getValue().result.asString());
        List<tableRow> list = new ArrayList<>();
        for(FlowExecution flow : flowExecutions){
            list.add(new tableRow(flow.getFlowDefinition().getName(), flow.getStartedTime(), flow.getFlowExecutionResult().toString(),flow));
        }

        ObservableList<tableRow> rowList = FXCollections.observableArrayList(
                list
        );

        // Set the items of the TableView
        historyOfExecutionsFlowsTable.setItems(rowList);

        // Add event handler for row selection
        historyOfExecutionsFlowsTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                tableRow flowDetails = historyOfExecutionsFlowsTable.getSelectionModel().getSelectedItem();
                if (flowDetails != null) {
                    if(flowDetails.getTheFlow().getFlowExecutionResult() != FlowExecutionResult.PROCESSING)
                        showFlowDetails(flowDetails.getTheFlow());
                }
            }
        });


    }

    private void showFlowDetails(FlowExecution flow) {
        flowChooseName.setText(flow.getFlowDefinition().getName());
        flowChooseId.setText(flow.getUniqueId());
        freeInputsList.getItems().clear();
        flowOutputsList.getChildren().clear();

        if(flow.getAllFreeInputsWithDataToPrintList().size() == 0) {
            freeInputsList.getItems().add("No free inputs in the flow");
        }
        else{
            flow.getAllFreeInputsWithDataToPrintList().stream().forEach(input -> freeInputsList.getItems().add(input));
        }

        List<FlowExecution.flowOutputsData> outPuts = flow.getOutputsStepData();
        List<String> outputsDetails = flow.getAllOutPutsWithDataToPrintList();
        if(outputsDetails.size() == 0)
            flowOutputsList.getChildren().add(new Label("No outputs during the flow"));
        else{
            int i = 1;
            boolean noOutput = false;
            for(FlowExecution.flowOutputsData output : outPuts){
                String outputLine = i + "." + output.getFinalName() + "," + output.getOutputDD().userString() + "(" + output.getOutputDD().dataDefinition().getName() + ")";
                if(output.getData().getClass() == String.class) {
                    if (output.getData().equals("Not created due to failure in flow")) {
                        outputLine += "-NOTE:Not created due to failure in flow";
                        noOutput = true;
                    }
                }
                if(!noOutput) {
                    Label outText;
                    if (output.getOutputDD().dataDefinition() == DataDefinitionRegistry.LIST) {
                        outText = new Label(outputLine);
                        HBox hbox = new HBox();
                        hbox.setSpacing(5);
                        hbox.getChildren().add(outText);
                        Hyperlink hyperlink = new Hyperlink("Show list");
                        hyperlink.setOnAction(event -> showListPopup(output));
                        hbox.getChildren().add(hyperlink);
                        flowOutputsList.getChildren().add(hbox);
                    } else if (output.getOutputDD().dataDefinition() == DataDefinitionRegistry.RELATION) {
                        outText = new Label(outputLine);
                        HBox hbox = new HBox();
                        hbox.setSpacing(5);
                        hbox.getChildren().add(outText);
                        Hyperlink hyperlink = new Hyperlink("Show table");
                        hyperlink.setOnAction(event -> showTablePopup(output));
                        hbox.getChildren().add(hyperlink);
                        flowOutputsList.getChildren().add(hbox);
                    } else {
                        outputLine += "\nData:" + output.getData().toString();
                        outText = new Label(outputLine);
                        flowOutputsList.getChildren().add(outText);
                    }
                    i++;
                }
            }
        }

        stepDetailsTree.setRoot(new TreeItem<>());
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
                for (String log : data.logs.getStepLogs())
                    totalLogs.getChildren().add(new TreeItem<>(log));
                stepNameItem.getChildren().addAll(stepDetails,stepSummaryLine,totalLogs);
                stepDetailsTree.getRoot().getChildren().addAll(stepNameItem);
            }
            else {
                stepDetails = new TreeItem<>("Don't have data during failure of the step");
                stepNameItem.getChildren().addAll(stepDetails);
                stepDetailsTree.getRoot().getChildren().addAll(stepNameItem);
            }
        }
        flowChooseVBOX.setVisible(true);
    }

    private void showTablePopup(FlowExecution.flowOutputsData flowData) {
        Stage popupStage = new Stage();
        TableView<ObservableList<StringPropertyWrapper>> tableView = new TableView<>();
        tableView.setColumnResizePolicy(historyOfExecutionsFlowsTable.getColumnResizePolicy());

        ObservableList<ObservableList<StringPropertyWrapper>> data = FXCollections.observableArrayList();
        RelationData table = (RelationData) flowData.getData();

        for (String columnName : table.getColumns()) {
            final int columnIndex = table.getColumns().indexOf(columnName);
            TableColumn<ObservableList<StringPropertyWrapper>, String> column = new TableColumn<>(columnName);
            column.setCellValueFactory(cellData ->
                    cellData.getValue().get(columnIndex).getValueProperty());

            column.setCellFactory(TextFieldTableCell.forTableColumn());

            tableView.getColumns().add(column);
        }
        tableView.setItems(data);

        for(int i = 0; i < table.getRowsSize(); i++) {
            ObservableList<StringPropertyWrapper> row = FXCollections.observableArrayList();
            for (String value : table.getRowDataByColumnsOrder(i)) {
                row.add(new StringPropertyWrapper(value));
            }
            data.add(row);
        }
        popupStage.setTitle(flowData.getFinalName() + " output table");
        Scene popupScene = new Scene(tableView);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    // Method to show the list in a pop-up window
    private void showListPopup(FlowExecution.flowOutputsData flowData) {
        Stage popupStage = new Stage();
        ListView<String> tableView = new ListView<>();
        List<Object> list = ListData.class.cast(flowData.getData()).getList();
        list.forEach(item -> tableView.getItems().add(item.toString()));

        popupStage.setTitle(flowData.getFinalName() + " output list");
        Scene popupScene = new Scene(tableView);
        popupStage.setScene(popupScene);
        popupStage.show();
    }

    @Override
    public void setBodyController(BodyController bodyCTRL) {
        bodyForHistoryExecutionController = bodyCTRL;
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
