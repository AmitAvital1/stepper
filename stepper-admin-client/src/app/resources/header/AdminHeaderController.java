package app.resources.header;

import app.resources.main.AdminAppMainConroller;
import app.resources.util.AdminConstants;
import app.resources.util.http.AdminHttpClientUtil;
import com.google.gson.Gson;
import dto.StepperDTO;
import dto.roles.RoleDTO;
import dto.roles.RolesDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import okhttp3.*;
import org.controlsfx.control.ToggleSwitch;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static app.resources.util.AdminConstants.ROLES;

public class AdminHeaderController {

    private AdminAppMainConroller mainController;
    @FXML
    private Button loadXmlButton;

    @FXML
    private TextField fileXmlPathTextField;

    @FXML
    private Button flowDefinitionButtom;
    @FXML
    private Button flowExecutionButton;
    @FXML
    private Button executionHistoryButton;
    @FXML
    private Button statisticsFlowsButton;
    @FXML
    private ToggleSwitch darkModeToggle;

    private final Integer LOAD_FAILED = 510;
    private final Integer ERROR_OCCURRED = 511;

    @FXML
    public void initialize() {
        String finalUrl = HttpUrl
                .parse(AdminConstants.LOAD_XML)
                .newBuilder()
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        AdminHttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    Gson gson = new Gson();
                    String rawBody = response.body().string();
                    StepperDTO stepper = gson.fromJson(rawBody, StepperDTO.class);
                    mainController.addFlows(stepper.getFlows());
                }
            }
        });
    }

    @FXML
    void loadXmlButton(ActionEvent event)  {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            boolean res = false;
          //  try{

                RequestBody body =
                        new MultipartBody.Builder()
                                .addFormDataPart("file", selectedFile.getName(), RequestBody.create(selectedFile, MediaType
                                        .parse("text/xml")))
                                .build();

                Request request = new Request.Builder()
                        .url(AdminConstants.LOAD_XML)
                        .post(body)
                        .build();

            AdminHttpClientUtil.runAsync(request, new Callback() {
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.code() == LOAD_FAILED) {
                            Platform.runLater(() -> {
                                try {
                                    showErrorPopup("Load Failed",response.body().string());
                                } catch (IOException e) {
                                    showErrorPopup("Load Failed","Error");
                                }
                            });
                        }
                        else if(response.code() == ERROR_OCCURRED){
                            Platform.runLater(() -> {
                            showErrorPopup("An error occurred","Error while trying get your data from XML");
                            });
                        }
                        else {
                            Gson gson = new Gson();
                            String rawBody = response.body().string();
                            StepperDTO stepper = gson.fromJson(rawBody, StepperDTO.class);
                            mainController.addFlows(stepper.getFlows());
                            Platform.runLater(() -> {
                                mainController.clearBodyScreen();
                                fileXmlPathTextField.setPromptText(selectedFile.getAbsolutePath());
                            });
                        }
                    }

                    public void onFailure(Call call, IOException e) {
                        System.out.println("Oopsy... something went wrong..." + e.getMessage());
                    }
                });
        }
    }
    public void setMainController(AdminAppMainConroller mainController) {
        this.mainController = mainController;
        darkModeToggle.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                // Switch to dark mode
                mainController.getPrimaryStage().getScene().getStylesheets().clear();
                mainController.getPrimaryStage().getScene().getStylesheets().add(getClass().getResource("/app/resources/main/dark.css").toExternalForm());
            } else {
                // Switch to light mode
                mainController.getPrimaryStage().getScene().getStylesheets().clear();
                mainController.getPrimaryStage().getScene().getStylesheets().add(getClass().getResource("/app/resources/main/style.css").toExternalForm());
            }
        });
    }
    private static void showErrorPopup(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    @FXML
    void flowDefinitionButtonListen(ActionEvent event) {
        resetButtonsColor();
        flowDefinitionButtom.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");
        mainController.showFlowDefinition();
    }
    @FXML
    void flowExecutionListener(ActionEvent event) {
        resetButtonsColor();
        flowExecutionButton.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");
        mainController.showFlowExectuion();
    }
    @FXML
    void executionHistoryButtonListen(ActionEvent event) {
        resetButtonsColor();
        executionHistoryButton.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");
        mainController.showFlowsHistory();
    }
    @FXML
    void statisticsFlowsButtonListen(ActionEvent event) {
        resetButtonsColor();
        statisticsFlowsButton.setStyle("-fx-background-color: #5482d0;" + "-fx-scale-x: 0.95;" + "-fx-scale-y: 0.95;");
        mainController.showFlowsStats();
    }

    public void setFlowHistory() {
        executionHistoryButton.setDisable(false);
    }

    public void resetButtonsColor(){
        flowDefinitionButtom.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);");
        flowExecutionButton.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);");
        statisticsFlowsButton.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);");
        executionHistoryButton.setStyle("-fx-background-color: linear-gradient(to right,#196BCA ,#6433E0);");
    }

    public void disableExecutionButton() {
        flowExecutionButton.setDisable(true);
    }
    public void enableExecutionButton() {
        flowExecutionButton.setDisable(false);
    }
}
