package app.resources.header;

import app.resources.main.AppMainConroller;
import app.resources.util.Constants;
import app.resources.util.http.HttpClientUtil;
import com.google.gson.Gson;
import dto.execution.FlowExecutionUUIDDTO;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import okhttp3.*;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.ToggleSwitch;
import org.jetbrains.annotations.NotNull;
import project.java.stepper.exceptions.StepperExeption;
import project.java.stepper.load.LoadStepperDataFromXml;

import javax.xml.bind.JAXBException;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;

import static app.resources.util.Constants.*;

public class HeaderController implements Closeable {

    private AppMainConroller mainController;
    @FXML
    private Button loginButton;
    private PopOver errorPopOver;

    @FXML
    private HBox loginHBox;

    @FXML
    private TextField usernameTextField;

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

    private HeaderDetailsRefresher headerDetailsRefresher;
    private Timer timer = null;

    private Label userNameDetails = new Label();
    private Label isManager = new Label("Is manager: ");
    private Label rolesDetails = new Label("Roles: ");

    @FXML
    void loginButtonAction(ActionEvent event) {

        String userName = usernameTextField.getText();
        if (userName.isEmpty()) {
            showErrorMessage(loginButton, "Please enter username.");
            return;
        }
        String finalUrl = HttpUrl
                .parse(LOGIN)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();

        Request request = new Request.Builder()
                .url(finalUrl)
                .build();

        HttpClientUtil.runAsync(request, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        showErrorMessage(loginButton,"Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            showErrorMessage(loginButton, responseBody)
                    );
                } else {
                    Platform.runLater(() -> {
                        loginHBox.getChildren().clear();
                        startHeaderRefresher();
                        loginHBox.getChildren().addAll(userNameDetails,isManager,rolesDetails);
                        loginHBox.setSpacing(15);
                        userNameDetails.setText("Name: " + userName);
                        flowDefinitionButtom.setDisable(false);
                        flowExecutionButton.setDisable(true);
                        statisticsFlowsButton.setDisable(false);
                        executionHistoryButton.setDisable(false);
                    });
                }
            }
        });
    }
    public void setMainController(AppMainConroller mainController) {
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
    public void startHeaderRefresher() {
        headerDetailsRefresher = new HeaderDetailsRefresher(isManager,rolesDetails);
        timer = new Timer();
        timer.schedule(headerDetailsRefresher, 0, REFRESH_RATE);
    }
    public void close(){
        if(timer != null) {
            timer.cancel();
            logout();
        }
    }

    private void logout() {
        String finalUrl = HttpUrl
                .parse(LOGIN)
                .newBuilder()
                .build()
                .toString();

        Gson gson = new Gson();
        String json = gson.toJson(new String("Dummy"));
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
                ;
            }
        });
    }
}
