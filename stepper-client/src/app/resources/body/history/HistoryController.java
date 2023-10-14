package app.resources.body.history;

import app.resources.body.BodyController;
import app.resources.body.BodyControllerDefinition;
import app.resources.util.http.HttpClientUtil;
import com.google.gson.Gson;
import dto.FlowDefinitionDTO;
import dto.HistoryFlowsDTO;
import dto.StepUsageDeclarationImplDTO;
import dto.execution.FlowExecutionDTO;
import dto.execution.FlowExecutionUUIDDTO;
import dto.execution.StepExecutionContextDTO;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.*;
import org.controlsfx.control.PopOver;
import org.jetbrains.annotations.NotNull;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecutionResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static app.resources.util.Constants.*;

public class HistoryController implements BodyControllerDefinition {
    private BodyController bodyForHistoryExecutionController;
    @FXML
    private TableView<tableRow> historyOfExecutionsFlowsTable;
    @FXML
    private TableColumn<tableRow, String> nameColumn;
    @FXML
    private TableColumn<tableRow, String> usernameColumn;
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
    @FXML
    private GridPane gredContinuations;

    @FXML
    private HBox hboxContinuations;

    @FXML
    private Button continueToFlowButton;
    @FXML
    private ImageView rerunButtonImg;
    @FXML
    private ChoiceBox<String> sortTableChoice;


    private List<FlowExecutionDTO> flowExecutions  = new ArrayList<>();
    private List<FlowDefinition> flows;
    private PopOver errorPopOver;


    public class tableRow{
        private String username;
        private String flowName;
        private String executionTime;
        private ObjectProperty<FlowExecutionResult> result = new SimpleObjectProperty<>();
        private FlowExecutionDTO theFlow;

        public tableRow(String n, String e, String r, FlowExecutionDTO f,String username){
            this.username = username;
            flowName = n;
            executionTime = e;
            theFlow = f;
            result.set(theFlow.getFlowExecutionResult());
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setTheFlow(FlowExecutionDTO theFlow) {
            this.theFlow = theFlow;
        }

        public FlowExecutionDTO getTheFlow() {
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
    public void setFlowsDetails(List<FlowDefinition> flow, List<FlowDefinitionDTO> flowDTO) {
        flows = flow;
    }

    @Override
    public void show() {
        flowChooseVBOX.setVisible(false);
        getExecutionHistoryReq();
        usernameColumn.setCellValueFactory(new PropertyValueFactory<tableRow, String>("username"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<tableRow, String>("flowName"));
        executionTimeColum.setCellValueFactory(new PropertyValueFactory<tableRow, String>("executionTime"));
        //resultColumn.setCellValueFactory(new PropertyValueFactory<tableRow, String>("result"));
        resultColumn.setCellValueFactory(cellData -> cellData.getValue().result.asString());
    }

    private void getExecutionHistoryReq() {
        String finalUrl = HttpUrl
                .parse(EXECUTION_HISTORY)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String rawBody = response.body().string();
                    HistoryFlowsDTO history = gson.fromJson(rawBody, HistoryFlowsDTO.class);
                    flowExecutions = history.getFlows();
                    Platform.runLater(() -> {
                    List<tableRow> list = new ArrayList<>();
                    for(FlowExecutionDTO flow : flowExecutions){
                        list.add(new tableRow(flow.getFlowDefinition().getName(), flow.getStartedTime(), flow.getFlowExecutionResult().toString(),flow,flow.getUsernameExe()));
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
                        sortTableChoice.getItems().addAll("ALL","SUCCESS", "FAILURE", "WARNING");
                        sortTableChoice.setValue("ALL");
                        sortTableChoice.setOnAction(event -> sortTable(historyOfExecutionsFlowsTable,sortTableChoice.getValue(),rowList));
                    });
                }
            }
        });
    }

    private void sortTable(TableView<tableRow> historyOfExecutionsFlowsTable, String value, ObservableList<tableRow> rowList) {
        historyOfExecutionsFlowsTable.setItems(rowList);
        FilteredList<tableRow> filteredData = new FilteredList<>(historyOfExecutionsFlowsTable.getItems());
        String sort = value;
        Predicate<tableRow> filterPredicate;
        if(value.equals("ALL"))
            filterPredicate = row -> true;
        else
            filterPredicate = row -> row.getResult().contains(value);
        filteredData.setPredicate(filterPredicate);
        historyOfExecutionsFlowsTable.setItems(filteredData);
    }

    private void showFlowDetails(FlowExecutionDTO flow) {
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

        List<FlowExecutionDTO.flowOutputsDataDTO> outPuts = flow.getOutputsStepData();
        List<String> outputsDetails = flow.getAllOutPutsWithDataToPrintList();
        if(outputsDetails.size() == 0)
            flowOutputsList.getChildren().add(new Label("No outputs during the flow"));
        else{
            int i = 1;
            boolean noOutput = false;
            for(FlowExecutionDTO.flowOutputsDataDTO output : outPuts){
                String outputLine = i + "." + output.getFinalName() + "," + output.getOutputDD().userString() + "(" + output.getOutputDD().dataDefinition().getName() + ")";
                if(!output.getCreatedFromFlow()) {
                    outputLine += "-NOTE:Not created due to failure in flow";
                    noOutput = true;
                }
                Label outText;
                if(!noOutput) {
                    if (output.getOutputDD().dataDefinition().getType().equals("ListData")) {
                        outText = new Label(outputLine);
                        HBox hbox = new HBox();
                        hbox.setSpacing(5);
                        hbox.getChildren().add(outText);
                        Hyperlink hyperlink = new Hyperlink("Show list");
                        hyperlink.setOnAction(event -> showListPopup(output));
                        hbox.getChildren().add(hyperlink);
                        flowOutputsList.getChildren().add(hbox);
                    } else if (output.getOutputDD().dataDefinition().getType().equals("RelationData")) {
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
                }else{
                    outText = new Label(outputLine);
                    flowOutputsList.getChildren().add(outText);
                }
                i++;
            }
        }

        stepDetailsTree.setRoot(new TreeItem<>());
        for(StepUsageDeclarationImplDTO step : flow.getFlowDefinition().getFlowSteps()){
            StepExecutionContextDTO.stepDataDTO data = flow.getFlowContexts().getStepData(step);
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
        if(flow.getFlowsContinuation().size() <= 0)
            gredContinuations.setVisible(false);
        else{
            gredContinuations.setVisible(true);
            continueToFlowButton.setDisable(true);
            hboxContinuations.getChildren().clear();
            ToggleGroup toggleGroup = new ToggleGroup();
            for(String cDetails : flow.getFlowsContinuation()) {
                RadioButton radioButton = new RadioButton(cDetails);
                radioButton.setToggleGroup(toggleGroup);
                radioButton.setOnAction(event -> {
                    if (radioButton.isSelected()) {
                        continueToFlowButton.setDisable(false);
                        continueToFlowButton.setOnAction(event1 -> {runContinuationFlowFlow(flow.getUniqueId(),cDetails,continueToFlowButton);});
                    }
                });
                hboxContinuations.getChildren().add(radioButton);
            }
        }
        rerunButtonImg.setOnMouseClicked(e -> rerunFlow(flow.getUniqueId(),rerunButtonImg));
        Tooltip tooltip = new Tooltip();
        rerunButtonImg.setOnMouseEntered(event -> {
            tooltip.setText("Rerun flow");
            tooltip.show(rerunButtonImg, event.getScreenX() + 5, event.getScreenY() + 5);
        });
        rerunButtonImg.setOnMouseExited(event -> {
            tooltip.hide();
        });
        flowChooseVBOX.setVisible(true);
    }

    private void showTablePopup(FlowExecutionDTO.flowOutputsDataDTO flowData) {
        Stage popupStage = new Stage();
        TableView<ObservableList<StringPropertyWrapper>> tableView = new TableView<>();
        tableView.setColumnResizePolicy(historyOfExecutionsFlowsTable.getColumnResizePolicy());

        ObservableList<ObservableList<StringPropertyWrapper>> data = FXCollections.observableArrayList();
        RelationData table = flowData.getItsRelation();

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
    private void showListPopup(FlowExecutionDTO.flowOutputsDataDTO flowData) {
        Stage popupStage = new Stage();
        ListView<String> tableView = new ListView<>();
        List<Object> list = (List<Object>)flowData.getData();
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
    private void runContinuationFlowFlow(String UUID,String flowNameToContinue,Button continueButton) {
        //Continuation behaving as rerun so using this function
        String finalUrl = HttpUrl
                .parse(EXECUTION_CONTINUATION)
                .newBuilder()
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new FlowExecutionUUIDDTO(UUID,flowNameToContinue));//Send new name of the flow
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showErrorMessage(continueButton, e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody = response.body().string();
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    FlowExecutionDTO newFlow = gson.fromJson(rawBody, FlowExecutionDTO.class);
                    Platform.runLater(() -> {
                        bodyForHistoryExecutionController.executeContinuationFlowScreen(newFlow);
                    });
                }
                else{
                    Platform.runLater(() -> {
                        showErrorMessage(continueButton,rawBody);
                    });
                }
            }
        });
    }
    private void rerunFlow(String flowUUID, ImageView rerunButtonImg) {
        //Continuation behaving as rerun so using this function
        String finalUrl = HttpUrl
                .parse(EXECUTION_RERUN)
                .newBuilder()
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new FlowExecutionUUIDDTO(flowUUID));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showErrorMessage(rerunButtonImg, e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody = response.body().string();
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    FlowExecutionDTO newFlow = gson.fromJson(rawBody, FlowExecutionDTO.class);
                    Platform.runLater(() -> {
                        bodyForHistoryExecutionController.executeContinuationFlowScreen(newFlow);
                    });
                }
                else{
                    Platform.runLater(() -> {
                        showErrorMessage(rerunButtonImg,rawBody);
                    });
                }
            }
        });
    }
    private void showErrorMessage(Node button, String message) {
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

}
