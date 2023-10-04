package app.resources.body.execution;

import app.resources.body.BodyController;
import app.resources.body.BodyControllerDefinition;
import app.resources.util.http.HttpClientUtil;
import com.google.gson.Gson;
import dto.DataDefinitionDeclarationDTO;
import dto.FlowDefinitionDTO;
import dto.StepUsageDeclarationImplDTO;
import dto.execution.*;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.*;
import okhttp3.*;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.SegmentedButton;
import org.jetbrains.annotations.NotNull;
import project.java.stepper.dd.impl.SqlFilter.SqlFilter;
import project.java.stepper.dd.impl.list.ListData;
import project.java.stepper.dd.impl.relation.RelationData;
import project.java.stepper.flow.definition.api.FlowDefinition;
import project.java.stepper.flow.execution.FlowExecutionResult;
import javafx.util.Duration;
import javafx.animation.PauseTransition;


import java.io.File;
import java.io.IOException;
import java.util.*;

import project.java.stepper.step.api.DataNecessity;
import project.java.stepper.step.api.UIDDPresent;

import static app.resources.util.Constants.*;

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

    List<FlowDefinitionDTO> flowsDTO;
    private PopOver errorPopOver;
    private List<Button> allFlowsButtons = new ArrayList<>();

    private String UUID;
    private Timer timer;
    private FlowExecutionDTO currentFlowRunning = null;

    @Override
    public void setFlowsDetails(List<FlowDefinition> flow, List<FlowDefinitionDTO> flowDTO) {
        flowsDTO = flowDTO;
    }

    public BorderPane getTheAllBorderOfExecute() {
        return theAllBorderOfExecute;
    }

    @Override
    public void show() {
        flowDetailsExecutionBox.setVisible(false);
        flowExecutionInfo.setVisible(false);
        if(bodyForFlowExecutionController.getCurrentUUID() != null){
            if(currentFlowRunning != null) {
                if(currentFlowRunning.getFlowExecutionResult() == FlowExecutionResult.PROCESSING) {
                    flowDetailsExecutionBox.setVisible(true);
                    flowExecutionInfo.setVisible(true);
                }
            }
        }
    }

    public void handleFlowButtonAction(FlowDefinitionDTO flowButton) {
        makeExecutionFlowMakeRequest(flowButton);
        addFlowDetails(flowButton);
        theVboxParent.getChildren().clear();
        theVboxParent.getChildren().addAll(flowDetailsExecutionBox,flowExecutionInfo);
        Map<HBox,Boolean> freeInputToMandatory = new HashMap<>();
        supllyFreeInput.setDisable(false);
        flowExecuteNameLabel.setDisable(false);
        freeInputsList.setDisable(false);
        flowExecutionInfo.setVisible(false);
        flowExecuteNameLabel.setText(flowButton.getName());
        freeInputsList.getChildren().clear();
        SegmentedButton segmentedButton = new SegmentedButton();
        VBox sql_filter_vbox = null;
        List<HBox> sqlFilterDetails = null;
        Map<String, DataDefinitionDeclarationDTO> freeInputs = flowButton.getFreeInputFinalNameToDD();
        for (Map.Entry<String, DataDefinitionDeclarationDTO> entry : freeInputs.entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclarationDTO dd = entry.getValue();
                HBox hbox = new HBox();
                hbox.setPadding(new Insets(10));
                if(!flowButton.getInitialValues().containsKey(key) || dd.UIPresent() == UIDDPresent.SQL_FILTER) {
                    Label stepName = new Label(key);
                    TextField textField = new TextField();
                    textField.setPromptText(dd.userString() + "[" + dd.dataDefinition().getName() + "]");
                    Button button = new Button("Add");
                    button.setOnAction(e -> handleFreeInputButtonAction(button, UUID, key, dd, textField));
                    if(dd.UIPresent() == UIDDPresent.FOLDER_DIALOG){
                        textField.setOnMouseClicked(e -> freeInputDialog(textField));
                    }else if(dd.UIPresent() == UIDDPresent.ENUM){
                        if(dd.dataDefinition().getType().equals("MethodEnum")){
                            ToggleButton button1 = new ToggleButton("GET");
                            ToggleButton button2 = new ToggleButton("PUT");
                            ToggleButton button3 = new ToggleButton("POST");
                            ToggleButton button4 = new ToggleButton("DELETE");
                            button1.setOnAction(e -> handleFreeInputEnumAction(button1, UUID, key, dd));
                            button2.setOnAction(e -> handleFreeInputEnumAction(button2, UUID, key, dd));
                            button3.setOnAction(e -> handleFreeInputEnumAction(button3, UUID, key, dd));
                            button4.setOnAction(e -> handleFreeInputEnumAction(button4, UUID, key, dd));

                            segmentedButton.getButtons().addAll(button1, button2, button3, button4);
                        }else if(dd.dataDefinition().getType().equals("ProtocolEnum")){
                            ToggleButton button1 = new ToggleButton("https");
                            ToggleButton button2 = new ToggleButton("http");
                            button1.setOnAction(e -> handleFreeInputEnumAction(button1, UUID, key, dd));
                            button2.setOnAction(e -> handleFreeInputEnumAction(button2, UUID, key, dd));
                            segmentedButton.getButtons().addAll(button1, button2);
                        }else if(dd.dataDefinition().getType().equals("ZipEnum")){
                            ToggleButton button1 = new ToggleButton("ZIP");
                            ToggleButton button2 = new ToggleButton("UNZIP");
                            button1.setOnAction(e -> handleFreeInputEnumAction(button1, UUID, key, dd));
                            button2.setOnAction(e -> handleFreeInputEnumAction(button2, UUID, key, dd));
                            segmentedButton.getButtons().addAll(button1, button2);
                        }
                    }else if(dd.UIPresent() == UIDDPresent.FILE_CHOOSER){
                        textField.setOnMouseClicked(e -> fileChooser(textField));
                    }else if(dd.UIPresent() == UIDDPresent.SQL_FILTER){
                        sql_filter_vbox = new VBox();
                        sqlFilterDetails = new ArrayList<>();
                        if(flowButton.getInitialValues().containsKey(key)){
                            SqlFilterDTO filter = flowButton.getInitialValueForSqlFilter().get(key);
                            for(String filterKey : filter.getKeys()){
                                HBox filterHbox = new HBox();
                                ComboBox<String> operatorComboBox = new ComboBox<>();
                                operatorComboBox.getItems().addAll("=", "LIKE");
                                TextField filterKeyText = new TextField(filterKey);
                                TextField value = new TextField();
                                value.setPromptText("Enter value");
                                filterKeyText.setDisable(true);
                                if(filter.getOperation(filterKey) != null) {
                                    operatorComboBox.setValue(filter.getOperation(filterKey));
                                    operatorComboBox.setDisable(true);
                                }
                                if(filter.getValue(filterKey) != null){
                                    value.setText(filter.getValue(filterKey));
                                    value.setDisable(true);
                                }
                                filterHbox.setSpacing(5);
                                filterHbox.setAlignment(Pos.CENTER);
                                filterHbox.getChildren().addAll(filterKeyText,operatorComboBox,value);
                                sql_filter_vbox.getChildren().add(filterHbox);
                                sqlFilterDetails.add(filterHbox);
                            }
                        }
                        else{
                            HBox filterHbox = new HBox();
                            ComboBox<String> operatorComboBox = new ComboBox<>();
                            operatorComboBox.getItems().addAll("=", "LIKE");
                            TextField filterKeyText = new TextField();
                            filterKeyText.setPromptText("Add search key");
                            TextField value = new TextField();
                            value.setPromptText("Enter value");
                            filterHbox.setSpacing(5);
                            filterHbox.setAlignment(Pos.CENTER);
                            filterHbox.getChildren().addAll(filterKeyText,operatorComboBox,value);
                            sql_filter_vbox.getChildren().add(filterHbox);
                            sqlFilterDetails.add(filterHbox);
                        }
                    }
                    Label isMandatory = new Label(dd.necessity().toString());
                    if (dd.necessity() == DataNecessity.MANDATORY)
                        executeFlowButtonFinish.setDisable(true);
                    else{
                        isMandatory.setStyle("-fx-text-fill: rgba(168,3,119,0.75)");
                    }
                    hbox.setSpacing(5);
                    textField.setMaxWidth(250);
                    hbox.setHgrow(textField, Priority.ALWAYS);
                    segmentedButton.setMinWidth(250);
                    if(dd.UIPresent() == UIDDPresent.ENUM)
                        hbox.getChildren().addAll(stepName, segmentedButton, isMandatory);
                    else if(dd.UIPresent() == UIDDPresent.SQL_FILTER) {
                        List<HBox> finalSqlFilterDetails = sqlFilterDetails;
                        VBox finalSql_filter_vbox = sql_filter_vbox;
                        button.setAlignment(Pos.CENTER);
                        isMandatory.setAlignment(Pos.CENTER);
                        hbox.setAlignment(Pos.CENTER_LEFT);
                        button.setOnAction(e -> handleSqlFreeInputButtonAction(button, UUID, key, dd, finalSqlFilterDetails, finalSql_filter_vbox));
                        hbox.getChildren().addAll(stepName, sql_filter_vbox, button, isMandatory);
                    }
                    else
                        hbox.getChildren().addAll(stepName, textField, button, isMandatory);
                    freeInputToMandatory.put(hbox,dd.necessity() == DataNecessity.MANDATORY ? true : false);
            }
        }
        freeInputToMandatory.entrySet().stream().forEach(set -> {if(set.getValue() == true) freeInputsList.getChildren().add(set.getKey());});
        Label dummy = new Label("Dummy");
        dummy.setVisible(false);
        freeInputsList.getChildren().add(dummy);
        freeInputToMandatory.entrySet().stream().forEach(set -> {if(set.getValue() == false) freeInputsList.getChildren().add(set.getKey());});
        //checkValidToExecute(executeFlowButtonFinish);
        executeFlowButtonFinish.setOnAction(e -> executeFlow());

       flowDetailsExecutionBox.setVisible(true);
    }

    private void fileChooser(TextField textField) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Save Directory");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Save File");
            dialog.setHeaderText("Enter file name with extension:");
            dialog.setContentText("File Name:");

            // Show the text input dialog and wait for the user's input
            dialog.showAndWait().ifPresent(fileName -> textField.setText(selectedDirectory.getAbsolutePath() + "\\" + fileName));
        }
    }

    private void makeExecutionFlowMakeRequest(FlowDefinitionDTO flowButton) {
        String finalUrl = HttpUrl
                .parse(FLOW_EXECUTION)
                .newBuilder()
                .addQueryParameter("flowname", flowButton.getName())
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("FAIL");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String rawBody = response.body().string();
                    FlowExecutionUUIDDTO FlowExecutionUUIDDTO = gson.fromJson(rawBody, FlowExecutionUUIDDTO.class);
                    UUID = FlowExecutionUUIDDTO.getUuid();
                    checkValidToExecute(executeFlowButtonFinish);
                }
            }
        });
    }

    public void handleContinuationFlowButtonAction(FlowExecutionDTO flowExe) {
        addFlowDetails(flowExe.getFlowDefinition());
        UUID = flowExe.getUniqueId();
        theVboxParent.getChildren().clear();
        theVboxParent.getChildren().addAll(flowDetailsExecutionBox,flowExecutionInfo);
        FlowDefinitionDTO flowButton = flowExe.getFlowDefinition();
        supllyFreeInput.setDisable(false);
        flowExecuteNameLabel.setDisable(false);
        freeInputsList.setDisable(false);
        flowExecutionInfo.setVisible(false);
        flowExecuteNameLabel.setText(flowButton.getName());
        freeInputsList.getChildren().clear();
        Map<HBox,Boolean> freeInputToMandatory = new HashMap<>();
        Map<String, DataDefinitionDeclarationDTO> freeInputs = flowExe.getFlowDefinition().getFreeInputFinalNameToDD();
        SegmentedButton segmentedButton = new SegmentedButton();
        for (Map.Entry<String, DataDefinitionDeclarationDTO> entry : freeInputs.entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclarationDTO dd = entry.getValue();
            HBox hbox = new HBox();
            hbox.setPadding(new Insets(10));
            if (!flowButton.getInitialValues().containsKey(key)) {
                Label stepName = new Label(key);
                TextField textField = new TextField();
                Button button;
                if (flowExe.getStartersFreeInputForContext().containsKey(key)) {
                    textField.setPromptText(dd.userString() + "[" + dd.dataDefinition().getName() + "]");
                    textField.setText(flowExe.getStartersFreeInputForContext().get(key).toString());
                    button = new Button("Edit");
                    textField.setDisable(true);
                } else {
                    textField.setPromptText(dd.userString() + "[" + dd.dataDefinition().getName() + "]");
                    button = new Button("Add");
                }
                button.setOnAction(e -> handleFreeInputButtonAction(button, UUID, key, dd, textField));
                if(dd.UIPresent() == UIDDPresent.FOLDER_DIALOG){
                    textField.setOnMouseClicked(e -> freeInputDialog(textField));
                }else if(dd.UIPresent() == UIDDPresent.ENUM){
                    if(dd.dataDefinition().getType().equals("MethodEnum")){
                        ToggleButton button1 = new ToggleButton("GET");
                        ToggleButton button2 = new ToggleButton("PUT");
                        ToggleButton button3 = new ToggleButton("POST");
                        ToggleButton button4 = new ToggleButton("DELETE");
                        button1.setOnAction(e -> handleFreeInputEnumAction(button1, UUID, key, dd));
                        button2.setOnAction(e -> handleFreeInputEnumAction(button2, UUID, key, dd));
                        button3.setOnAction(e -> handleFreeInputEnumAction(button3, UUID, key, dd));
                        button4.setOnAction(e -> handleFreeInputEnumAction(button4, UUID, key, dd));
                        segmentedButton.getButtons().addAll(button1, button2, button3, button4);
                    }else if(dd.dataDefinition().getType().equals("ProtocolEnum")){
                        ToggleButton button1 = new ToggleButton("https");
                        ToggleButton button2 = new ToggleButton("http");
                        button1.setOnAction(e -> handleFreeInputEnumAction(button1, UUID, key, dd));
                        button2.setOnAction(e -> handleFreeInputEnumAction(button2, UUID, key, dd));
                        segmentedButton.getButtons().addAll(button1, button2);
                    }else if(dd.dataDefinition().getType().equals("ZipEnum")){
                        ToggleButton button1 = new ToggleButton("ZIP");
                        ToggleButton button2 = new ToggleButton("UNZIP");
                        button1.setOnAction(e -> handleFreeInputEnumAction(button1, UUID, key, dd));
                        button2.setOnAction(e -> handleFreeInputEnumAction(button2, UUID, key, dd));
                        segmentedButton.getButtons().addAll(button1, button2);
                    }
                }else if(dd.UIPresent() == UIDDPresent.FILE_CHOOSER){
                    textField.setOnMouseClicked(e -> fileChooser(textField));
                }
                Label isMandatory = new Label(dd.necessity().toString());
                if (dd.necessity() == DataNecessity.MANDATORY)
                    executeFlowButtonFinish.setDisable(true);
                else{
                    isMandatory.setStyle("-fx-text-fill: rgba(168,3,119,0.75)");
                }
                hbox.setSpacing(5);
                textField.setMaxWidth(250);
                hbox.setHgrow(textField, Priority.ALWAYS);
                segmentedButton.setMinWidth(250);
                if(dd.UIPresent() == UIDDPresent.ENUM) {
                    if (flowExe.getStartersFreeInputForContext().containsKey(key)) {
                        segmentedButton.getButtons().stream().filter(b -> b.getText().toUpperCase().equals(flowExe.getStartersFreeInputForContext().get(key).toString())).findFirst().get().setSelected(true);
                    }
                    hbox.getChildren().addAll(stepName, segmentedButton, isMandatory);
                }
                else
                    hbox.getChildren().addAll(stepName, textField, button, isMandatory);
                freeInputToMandatory.put(hbox,dd.necessity() == DataNecessity.MANDATORY ? true : false);
            }
        }
        freeInputToMandatory.entrySet().stream().forEach(set -> {if(set.getValue() == true) freeInputsList.getChildren().add(set.getKey());});
        Label dummy = new Label("Dummy");
        dummy.setVisible(false);
        freeInputsList.getChildren().add(dummy);
        freeInputToMandatory.entrySet().stream().forEach(set -> {if(set.getValue() == false) freeInputsList.getChildren().add(set.getKey());});

        executeFlowButtonFinish.setOnAction(e -> executeFlow());
        checkValidToExecute(executeFlowButtonFinish);
        flowDetailsExecutionBox.setVisible(true);
    }

    private void checkValidToExecute(Button executeFlowButtonFinish) {
        String finalUrl = HttpUrl
                .parse(FLOW_EXECUTION)
                .newBuilder()
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new FreeInputDTO(null, null, UUID));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() == 200) {
                    Platform.runLater(() -> {
                        executeFlowButtonFinish.setDisable(false);
                    });
                }
            }
        });
    }
    private void handleSqlFreeInputButtonAction(Button button, String uuid, String inputFinalName, DataDefinitionDeclarationDTO dd, List<HBox> sqlFilterDetails, VBox sql_filter_vbox) {
        if(button.getText() == "Edit"){
            button.setText("Add");
            sql_filter_vbox.setDisable(false);
            if(dd.necessity() == DataNecessity.MANDATORY)
                executeFlowButtonFinish.setDisable(true);
            return;
        }
        int counter = 0;
        String freeInput = "";
        for(HBox filterHbox : sqlFilterDetails){
            TextField filterKey = (TextField)filterHbox.getChildren().get(0);
            String operator = ((ComboBox<String>) filterHbox.getChildren().get(1)).getValue();
            TextField filterValue = (TextField)filterHbox.getChildren().get(2);
            if(filterKey.getText().trim().isEmpty()){
                showErrorMessage(button,"Missing filter key data.");
                return;
            }
            if(operator == null){
                showErrorMessage(button,"Missing filter operator.");
                return;
            }
            if(filterValue.getText().trim().isEmpty()){
                showErrorMessage(button,"Missing filter value data.");
                return;
            }
            counter++;
            freeInput += filterKey.getText() + "," + ((operator.equals("=")) ? "EQUAL" : operator) + "," + filterValue.getText();
            if(counter < sqlFilterDetails.size())
                freeInput += "|";
        }
        String finalUrl = HttpUrl
                .parse(FLOW_EXECUTION)
                .newBuilder()
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new FreeInputDTO(inputFinalName, freeInput, uuid));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    showErrorMessage(button, e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() == 200) {
                    Platform.runLater(() -> {
                        sql_filter_vbox.setDisable(true);
                        button.setText("Edit");
                        executeFlowButtonFinish.setDisable(false);
                    });
                } else if (response.code() == 402) {
                    Platform.runLater(() -> {
                        sql_filter_vbox.setDisable(true);
                        button.setText("Edit");
                    });
                } else if (response.code() == 403) {
                    Platform.runLater(() -> {
                        showErrorMessage(button, responseBody);
                    });
                }
            }
        });

    }
    private void handleFreeInputButtonAction(Button button, String flowUUID, String inputFinalName, DataDefinitionDeclarationDTO dd, TextField textField) {
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
            String finalUrl = HttpUrl
                    .parse(FLOW_EXECUTION)
                    .newBuilder()
                    .build()
                    .toString();

            Gson gson = new Gson();
            String json = gson.toJson(new FreeInputDTO(inputFinalName, text, flowUUID));
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .post(requestBody)
                    .build();

            HttpClientUtil.runAsync(request, new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Platform.runLater(() -> {
                        showErrorMessage(button, e.getMessage());
                    });
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseBody = response.body().string();
                    if (response.code() == 200) {
                        Platform.runLater(() -> {
                            textField.setDisable(true);
                            button.setText("Edit");
                            executeFlowButtonFinish.setDisable(false);
                        });
                    } else if (response.code() == 402) {
                        Platform.runLater(() -> {
                            textField.setDisable(true);
                            button.setText("Edit");
                        });
                    } else if (response.code() == 403) {
                        Platform.runLater(() -> {
                            showErrorMessage(button, responseBody);
                        });
                    }
                }
            });
    }
    private void handleFreeInputEnumAction(ToggleButton button, String flowUUID, String inputFinalName, DataDefinitionDeclarationDTO dd) {
        if(!button.isSelected()) {
            if(dd.necessity() == DataNecessity.MANDATORY)
                executeFlowButtonFinish.setDisable(true);
            return;
        }
        String text = button.getText();
        String finalUrl = HttpUrl
                .parse(FLOW_EXECUTION)
                .newBuilder()
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new FreeInputDTO(inputFinalName, text, flowUUID));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .post(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() -> {
                    showErrorMessage(button, e.getMessage());
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseBody = response.body().string();
                if (response.code() == 200) {
                    Platform.runLater(() -> {
                        executeFlowButtonFinish.setDisable(false);
                    });
                } else if (response.code() == 403) {
                    Platform.runLater(() -> {
                        showErrorMessage(button, responseBody);
                    });
                }
            }
        });
    }

    private void showErrorMessage(Node button,String message) {
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


    private void executeFlow() {
        /*
        This function execute flow the user chose and validate all the free inputs has entered. then execute the flow.
         */

        putInAccordionTheExecuteDetails();
        supllyFreeInput.setDisable(true);
        flowExecuteNameLabel.setDisable(true);
        freeInputsList.setDisable(true);
        flowExecutionInfo.setVisible(true);


        Node n = HBOXProccesing.getChildren().get(0);
        HBOXProccesing.getChildren().clear();
        ProgressBar flowProgressBar = new ProgressBar(0);
        Label stepResLabel = new Label("");
        HBOXProccesing.getChildren().addAll(n,flowProgressBar,stepResLabel);
        contAndFormalStack.getChildren().clear();
        VBox formalOutPutsVbox = new VBox();
        VBox continuationVBOX = new VBox();
        continuationVBOX.setSpacing(20);
        VBox formalAndCont = new VBox(formalOutPutsVbox,continuationVBOX);
        formalAndCont.setSpacing(10);
        contAndFormalStack.getChildren().addAll(formalAndCont);
        stepsProgressTreeView.setRoot(new TreeItem<>());
        executeRequest();//Executing

        bodyForFlowExecutionController.enableExecutionButton();
        bodyForFlowExecutionController.getMainController().setHistoryButton();

        listOfLogsStackPane.getChildren().clear();
        ListView<String> listOfLogs = new ListView<>();
        listOfLogsStackPane.getChildren().add(listOfLogs);

        FlowExecutionRefresher refresher = new FlowExecutionRefresher(flowProgressBar, stepResLabel, formalOutPutsVbox,listOfLogs,continuationVBOX);
        timer = new Timer();
        timer.schedule(refresher, 0, 200);
        bodyForFlowExecutionController.setCurrentUUID(UUID);

        executeFlowButtonFinish.setText("Rerun flow");
        executeFlowButtonFinish.setOnAction(e -> rerunFlow(UUID,executeFlowButtonFinish));
    }

    private void executeRequest() {
        //Sending to the server to execute the flow
        String finalUrl = HttpUrl
                .parse(FLOW_EXECUTION)
                .newBuilder()
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new FlowExecutionUUIDDTO(UUID));
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

        Request request = new Request.Builder()
                .url(finalUrl)
                .put(requestBody)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //TODO FAILURE
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {

                }
                else if (response.code() == 402) {
                    //Missing mandatory input
                }
            }
        });
    }


    private void addFlowDetails(FlowDefinitionDTO flowButton) {
        HBox flowNameRead = new HBox();
        flowNameRead.setSpacing(5);
        ImageView readOnlyImage = new ImageView("app/resources/img/read-only.png");
        addReadOnly(flowButton,readOnlyImage);
        Label FlowNameTL = new Label();
        flowNameRead.getChildren().addAll(FlowNameTL,readOnlyImage);
        TreeView<String> flowDetailsTreeView = new TreeView<>();
        FlowNameTL.setText(flowButton.getName());
        FlowNameTL.getStyleClass().add("names");

        flowDetailsTreeView.setRoot(new TreeItem<>());
        TreeItem<String> formalOutputsItem = new TreeItem<>("Formal outputs");

        for(Map.Entry<String, DataDefinitionDeclarationDTO> entry : flowButton.getFormalOutput().entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclarationDTO value = entry.getValue();
            formalOutputsItem.getChildren().add(new TreeItem<>(key + ":" + value.userString()));
        }

        TreeItem<String> flowStepsItem = new TreeItem<>("Flow steps");
        for(StepUsageDeclarationImplDTO step : flowButton.getFlowSteps())
        {
            if(step.getFinalStepName().equals(step.getStepDefinition().name()))
                flowStepsItem.getChildren().add(new TreeItem<>(step.getFinalStepName() + ", Read Only:" + step.getStepDefinition().isReadonly()));
            else
                flowStepsItem.getChildren().add(new TreeItem<>(step.getStepDefinition().name() + " Alias to:" + step.getFinalStepName() + ", Read Only:" + step.getStepDefinition().isReadonly()));
        }
        TreeItem<String> freeInputsItem = new TreeItem<>("Free inputs");
        for(Map.Entry<String, DataDefinitionDeclarationDTO> entry : flowButton.getFreeInputFinalNameToDD().entrySet()) {
            String key = entry.getKey();
            DataDefinitionDeclarationDTO value = entry.getValue();
            freeInputsItem.getChildren().add(new TreeItem<>(key + ":" + value.userString() +
                    "(" + (!flowButton.getInitialValues().containsKey(key) ? value.necessity() : "INITIANAL") + ")"));
            freeInputsItem.getChildren().add(new TreeItem<>("Data type:" + value.dataDefinition().getName()));
            freeInputsItem.getChildren().add(new TreeItem<>(""));
        }
        flowDetailsTreeView.getRoot().getChildren().addAll(formalOutputsItem,flowStepsItem,freeInputsItem);
        flowDetailsTreeView.setShowRoot(false);
        flowDetailsTreeView.getStyleClass().add("flowDetailsTree");
        //flowDetailsTreeView.setMinWidth(500);
        flowDetailsTreeView.setPrefHeight(Region.USE_COMPUTED_SIZE);
        flowDetailsTreeView.refresh();
        flowListToExecute.getChildren().setAll(flowNameRead ,flowDetailsTreeView);
    }

    private void freeInputDialog(TextField textField) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Choose Directory");
        File selectedDir = directoryChooser.showDialog(bodyForFlowExecutionController.getMainController().getPrimaryStage());
        if(selectedDir != null)
            textField.setText(selectedDir.getAbsolutePath());
    }
    private void rerunFlow(String flowUUID,Button rerunButton) {
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
                        showErrorMessage(rerunButton, e.getMessage()));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String rawBody = response.body().string();
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    FlowExecutionDTO newFlow = gson.fromJson(rawBody, FlowExecutionDTO.class);
                    Platform.runLater(() -> {
                        bodyForFlowExecutionController.executeContinuationFlowScreen(newFlow);
                    });
                }
                else{
                    Platform.runLater(() ->
                            showErrorMessage(rerunButton,rawBody));
                }
            }
        });
    }

    private void addFormalOutputs(FlowExecutionDTO flow, VBox formalOutPutsVbox) {
        Map<String, Object> formalOutputToData = flow.getFormalOutPutsData();
        Label Title = new Label("Formal outputs:");
        formalOutPutsVbox.getChildren().add(Title);
        for (Map.Entry<String, Object> entry : formalOutputToData.entrySet()) {
            Label outText;
            if (entry.getValue() == null)
                continue;

            if (flow.getTypeOfFormalOutPut(entry.getKey()).equals("ListData")) {
                outText = new Label(entry.getKey());
                HBox hbox = new HBox();
                hbox.setSpacing(5);
                hbox.getChildren().add(outText);
                Hyperlink hyperlink = new Hyperlink("Show list");
                hyperlink.setOnAction(event -> showListPopup(entry.getValue()));
                hbox.getChildren().add(hyperlink);
                formalOutPutsVbox.getChildren().add(hbox);
            } else if (flow.getTypeOfFormalOutPut(entry.getKey()).equals("RelationData")) {
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
        Label dummyLabel = new Label("dummy");
        dummyLabel.setVisible(false);
        formalOutPutsVbox.getChildren().add(dummyLabel);
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
        //List<Object> list = ListData.class.cast(flowData).getList();
        List<Object> list = (List<Object>)flowData;
        list.forEach(item -> tableView.getItems().add(item.toString()));

        Scene popupScene = new Scene(tableView);
        popupStage.setScene(popupScene);
        popupStage.show();
    }
    private void showTablePopup(Object flowData) {
        Stage popupStage = new Stage();
        TableView<ObservableList<FlowsExecutionController.StringPropertyWrapper>> tableView = new TableView<>();

        ObservableList<ObservableList<FlowsExecutionController.StringPropertyWrapper>> data = FXCollections.observableArrayList();
        RelationData table = (RelationData)flowData;

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
    private void addReadOnly(FlowDefinitionDTO flowButton,ImageView readOnlyImage) {
        readOnlyImage.setFitHeight(23);
        readOnlyImage.setFitWidth(21);
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
    public class FlowExecutionRefresher extends TimerTask {

        private final ProgressBar flowProgressBar;
        private final Label stepResLabel;
        private final VBox formalOutPutsVbox;
        private final ListView<String> listOfLogs;
        private final VBox continuationVBOX;

        public FlowExecutionRefresher(ProgressBar flowProgressBar, Label stepResLabel, VBox formalOutPutsVbox, ListView<String> listOfLogs,VBox continuationVBOX) {
            this.flowProgressBar = flowProgressBar;
            this.stepResLabel = stepResLabel;
            this.formalOutPutsVbox = formalOutPutsVbox;
            this.listOfLogs = listOfLogs;
            this.continuationVBOX = continuationVBOX;
            stepsProgressTreeView.getRoot().getChildren().clear();
            listOfLogs.getItems().clear();
        }

        @Override
        public void run() {
            String finalUrl = HttpUrl
                    .parse(FLOW_EXECUTION_REFRESH)
                    .newBuilder()
                    .build()
                    .toString();

            Gson gson = new Gson();
            String json = gson.toJson(new FlowExecutionUUIDDTO(UUID));
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), json);

            Request request = new Request.Builder()
                    .url(finalUrl)
                    .post(requestBody)
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
                        FlowExecutionDTO flow = gson.fromJson(rawBody, FlowExecutionDTO.class);
                        currentFlowRunning = flow;
                        bodyForFlowExecutionController.setCurrentFlow(flow);
                        Platform.runLater(() -> {
                            flowProgressBar.setProgress(flow.getStepFinishedProperty().doubleValue() / ((double)flow.getFlowDefinition().getFlowSteps().size()));

                            if (flow.getFlowExecutionResult() != FlowExecutionResult.PROCESSING) {
                                if (flow.getUniqueId().equals(bodyForFlowExecutionController.getCurrentUUID())) {
                                    bodyForFlowExecutionController.disableExecutionButton();
                                }
                                stepResLabel.setText(flow.getFlowExecutionResult().name() + " [" + flow.getDuration() + ".ms" + "]");
                                if (flow.getFlowExecutionResult() == FlowExecutionResult.SUCCESS)
                                    stepResLabel.setStyle("-fx-text-fill: green;");
                                else if (flow.getFlowExecutionResult() == FlowExecutionResult.WARNING)
                                    stepResLabel.setStyle("-fx-text-fill: yellow;");
                                else
                                    stepResLabel.setStyle("-fx-text-fill: red;");

                                addFormalOutputs(flow, formalOutPutsVbox);

                            if(flow.getFlowsContinuation().size() > 0) {
                                Label Title = new Label("Flow continuation:");
                                formalOutPutsVbox.getChildren().add(Title);
                                HBox continuationHbox = new HBox();
                                continuationHbox.setSpacing(5);
                                ToggleGroup toggleGroup = new ToggleGroup();
                                Button continuationButton = new Button("Continue to flow");
                                continuationButton.setDisable(true);
                                continuationButton.setAlignment(Pos.CENTER);
                                for (String cDetails : flow.getFlowsContinuation()) {
                                    RadioButton radioButton = new RadioButton(cDetails);
                                    radioButton.setToggleGroup(toggleGroup);
                                    radioButton.setOnAction(event -> {
                                        if (radioButton.isSelected()) {
                                            continuationButton.setDisable(false);
                                            continuationButton.setOnAction(event1 -> {
                                                runContinuationFlowFlow(cDetails,continuationButton);
                                            });
                                        }
                                    });
                                    continuationHbox.getChildren().add(radioButton);
                                }
                                continuationVBOX.getChildren().addAll(Title,continuationHbox,continuationButton);
                            }
                                timer.cancel();
                            }
                            //stepsProgressTreeView.getRoot().getChildren().clear();
                            //listOfLogs.getItems().clear();
                            for (StepUsageDeclarationImplDTO step : flow.getFlowDefinition().getFlowSteps()) {
                                StepExecutionContextDTO.stepDataDTO data = flow.getFlowContexts().getStepData(step);
                                if (stepsProgressTreeView.getRoot().getChildren().stream().noneMatch(t -> t.getValue().equals(step.getFinalStepName())))
                                {
                                    TreeItem<String> stepNameItem = new TreeItem<>(step.getFinalStepName());
                                    TreeItem<String> stepDetails;
                                    if (data != null) {

                                        if (!data.step.getFinalStepName().equals(data.step.getStepDefinition().name()))
                                            stepNameItem.setValue(step.getFinalStepName() + "(" + step.getStepDefinition().name() + ")");

                                        stepDetails = new TreeItem<>("Total Time:[" + data.time.toMillis() + ".ms]" + ", Result:" + data.result);
                                        TreeItem<String> stepSummaryLine = new TreeItem<>(data.stepSummaryLine);
                                        TreeItem<String> totalLogs = new TreeItem<>("Total logs(" + data.logs.getStepLogs().size() + ")");
                                        for (String log : data.logs.getStepLogs()) {
                                            totalLogs.getChildren().add(new TreeItem<>(log));
                                            listOfLogs.getItems().add(step.getFinalStepName() + ":" + log);
                                        }
                                        stepNameItem.getChildren().addAll(stepDetails, stepSummaryLine, totalLogs);
                                        stepsProgressTreeView.getRoot().getChildren().addAll(stepNameItem);
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }
    private void runContinuationFlowFlow(String flowNameToContinue,Button continueButton) {
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
                        bodyForFlowExecutionController.executeContinuationFlowScreen(newFlow);
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
}
